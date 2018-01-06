package com.robin.manager.model;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateData {

    private Map<String, BrokerNode> brokerNodeMap;

    private String leader;

    private String self;

    private Map<String, TopicWrap> topicWrapMap;

    public UpdateData() {
    }

    public UpdateData(Map<String, BrokerNode> brokerNodeMap, String leader, String self, Map<String, TopicWrap> topicWrapMap) {
        this.brokerNodeMap = brokerNodeMap;
        this.leader = leader;
        this.self = self;
        this.topicWrapMap = topicWrapMap;
    }

    public UpdateData(JSONObject json) {

        Map<String, JSONObject> tempMap = json.getObject("brokerNodeMap", Map.class);

        this.brokerNodeMap = new ConcurrentHashMap<>();

        Set<Map.Entry<String, JSONObject>> entries = tempMap.entrySet();
        Iterator<Map.Entry<String, JSONObject>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JSONObject> entry = iterator.next();
            this.brokerNodeMap.put(entry.getKey(), new BrokerNode(entry.getValue()));
        }

        this.leader = json.getString("leader");
        this.self = json.getString("self");

        this.topicWrapMap = new ConcurrentHashMap<>();
        Map<String, JSONObject> tempTopicWrapMap = json.getObject("topicWrapMap", Map.class);
        Set<Map.Entry<String, JSONObject>> entries1 = tempTopicWrapMap.entrySet();
        for (Map.Entry<String, JSONObject> entry : entries1) {
            this.topicWrapMap.put(entry.getKey(), new TopicWrap(entry.getValue()));
        }
    }

    public Map<String, BrokerNode> getBrokerNodeMap() {
        return brokerNodeMap;
    }

    public void setBrokerNodeMap(Map<String, BrokerNode> brokerNodeMap) {
        this.brokerNodeMap = brokerNodeMap;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public Map<String, TopicWrap> getTopicWrapMap() {
        return topicWrapMap;
    }

    public void setTopicWrapMap(Map<String, TopicWrap> topicWrapMap) {
        this.topicWrapMap = topicWrapMap;
    }

    public JSONObject toJson() {
        JSONObject data = new JSONObject();

        data.put("brokerNodeMap", brokerNodeMap);
        data.put("leader", leader);
        data.put("self", self);
        data.put("topicWrap", topicWrapMap);

        return data;
    }
}
