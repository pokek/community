package com.tencent.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tencent.community.dao.PostMapper;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    PostMapper postMapper;

    @Autowired
    SensitiveFilter filterTool;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                                .maximumSize(maxSize)
                                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                                .build(new CacheLoader<String, List<DiscussPost>>() {
                                    @Nullable
                                    @Override
                                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                                        if (key == null || key.length() == 0) {
                                            throw new IllegalArgumentException("参数错误!");
                                        }

                                        String[] params = key.split(":");
                                        if (params == null || params.length != 2) {
                                            throw new IllegalArgumentException("参数错误!");
                                        }

                                        int offset = Integer.valueOf(params[0]);
                                        int limit = Integer.valueOf(params[1]);

                                        // 二级缓存: Redis -> mysql

                                        logger.debug("load post list from DB.");
                                        return postMapper.discussPostByUserId(0, offset, limit, 1);
                                    }
                                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                                .maximumSize(maxSize)
                                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                                .build(new CacheLoader<Integer, Integer>() {
                                    @Nullable
                                    @Override
                                    public Integer load(@NonNull Integer key) throws Exception {
                                        logger.debug("load post rows from DB.");

                                        return postMapper.discussionPostAll(key);
                                    }
                                });
    }

    /*
      查询某页数据
     */
    public List<DiscussPost> discussPostByUserId(int userId, int row, int pageSize, int orderMode){
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(row + ":" + pageSize);
        }

        logger.debug("load post list from DB.");

        return postMapper.discussPostByUserId(userId, row, pageSize, orderMode);
    }

    /*
      查询总条数
     */
    public int findAllPost(int userId){
        if (userId == 0) {
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");
        return postMapper.discussionPostAll(userId);
    }


    public int addPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空值");
        }
        // 转义html格式文件防止页面崩坏
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(filterTool.filter(post.getTitle()));
        post.setContent(filterTool.filter(post.getContent()));
        // 插入数据库
        return postMapper.insertPost(post);
    }

    public DiscussPost findPostById(int id){
        return postMapper.selectPostById(id);
    }

    public int updatePostCommentCount(int id, int count){
        return postMapper.updatePostCommentCount(id, count);
    }

    public int updateType(int id, int type) {
        return postMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return postMapper.updateStatus(id, status);
    }


    public int updateScore(int postId, double score) {
        return postMapper.updateScore(postId, score);
    }
}
