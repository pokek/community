package com.tencent.community.event;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.Event;
import com.tencent.community.domain.Message;
import com.tencent.community.domain.elasticsearch.DiscussPostRepository;
import com.tencent.community.service.DiscussPostService;
import com.tencent.community.service.ElasticsearchService;
import com.tencent.community.service.MessageService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.JsonUtils;
import io.netty.channel.EventLoopGroup;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Component
public class EventConsumer implements CommunityConstant {

    Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    MessageService messageService;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    DiscussPostService discussPostService;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleEvent(ConsumerRecord record){
        // 校验参数是否合格
        if(record == null || record.value() == null){
            logger.error("消息为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 解析不成功
        if(event == null){
            logger.error("消息解析不成功");
            return;
        }
        // 封装成message
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setConversationId(record.topic());
        message.setStatus(0);
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());
        // content内容
        Map<String, Object> content = new HashMap<>();
        content.put("entityType", event.getEntityType());
        content.put("userId", event.getUserId());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handleMessagePublished(ConsumerRecord record){
        // 校验参数是否合格
        if(record == null || record.value() == null){
            logger.error("消息为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        // 解析不成功
        if(event == null){
            logger.error("消息解析不成功");
            return;
        }

        DiscussPost post = discussPostService.findPostById(event.getEntityId());
        elasticsearchService.addPostToEngine(post);
    }


    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        elasticsearchService.deletePostFromEngine(event.getEntityId());
    }

    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功: " + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败: " + e.getMessage());
        }

        // 启用定时器,监视该图片,一旦生成了,则上传至七牛云.
        UploadTask task = new UploadTask(fileName, suffix);
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
    }

    class UploadTask implements Runnable {

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;
        // 启动任务的返回值
        private Future future;
        // 开始时间
        private long startTime;
        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            // 生成失败
            if (System.currentTimeMillis() - startTime > 30000) {
                logger.error("执行时间过长,终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            // 上传失败
            if (uploadTimes >= 3) {
                logger.error("上传次数过多,终止任务:" + fileName);
                future.cancel(true);
                return;
            }

            String path = wkImageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                logger.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                // 设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", JsonUtils.getJsonString(0));
                // 生成上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                // 指定上传机房
                UploadManager manager = new UploadManager(new Configuration(Region.region0()));
                try {
                    // 开始上传图片
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/" + suffix.substring(1, suffix.length()), false);
                    // 处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("0")) {
                        logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                    } else {
                        logger.info(String.format("第%d次上传成功[%s].", uploadTimes, fileName));
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                }
            } else {
                logger.info("等待图片生成[" + fileName + "].");
            }
        }
    }
}
