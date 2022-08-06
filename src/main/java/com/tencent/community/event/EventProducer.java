package com.tencent.community.event;

import com.alibaba.fastjson.JSONObject;
import com.tencent.community.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendEvent(Event event){
        // 以json字符串形式发送，
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}

