package com.tencent.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@SpringBootTest
public class TestKafka {

    @Autowired
    KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.produce("quickstart-events", "你好");
        kafkaProducer.produce("quickstart-events", "kafka");
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


@Component
class KafkaProducer{

    @Autowired
    KafkaTemplate kafkaTemplate;

    public void produce(String topic, String message){
        kafkaTemplate.send(topic, message);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics = {"quickstart-events"})
    public void handleMessage(ConsumerRecord records){
        System.out.println(records.value());
    }
}



