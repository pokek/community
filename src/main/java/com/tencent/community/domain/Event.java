package com.tencent.community.domain;

import java.util.HashMap;
import java.util.Map;

public class Event {

    private String topic;

    private int userId;

    private int entityType;

    private int entityId;

    private int entityUserId;

    // 增加事件对象的拓展性
    private Map<String, Object> data = new HashMap<>();

    public Event() {}

    public String getTopic() {
        return topic;
    }

    // set之后返回Event对象，使得可以链式调用
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // 入参不为map集合，为key-value，方便调用，不用创建map对象，直接key，value;
    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
