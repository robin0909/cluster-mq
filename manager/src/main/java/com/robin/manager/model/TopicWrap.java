package com.robin.manager.model;

import com.alibaba.fastjson.JSONObject;

public class TopicWrap {

    private String topic;
    private int type;

    public TopicWrap() {
    }

    public TopicWrap(String topic, int type) {
        this.topic = topic;
        this.type = type;
    }

    public TopicWrap(JSONObject json) {
        this.topic = json.getString("topic");
        this.type = json.getInteger("type");
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
