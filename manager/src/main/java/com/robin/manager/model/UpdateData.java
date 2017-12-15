package com.robin.manager.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class UpdateData {

    private Map<String, BrokerNode> brokerNodeMap;

    private String leader;

    private String self;

    public UpdateData() {
    }

    public UpdateData(Map<String, BrokerNode> brokerNodeMap, String leader, String self) {
        this.brokerNodeMap = brokerNodeMap;
        this.leader = leader;
        this.self = self;
    }

    public UpdateData(JSONObject json) {
        this.brokerNodeMap = json.getObject("brokerNodeMap", Map.class);
        this.leader = json.getString("leader");
        this.self = json.getString("self");
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

    public JSONObject toJson() {
        JSONObject data = new JSONObject();

        data.put("brokerNodeMap", brokerNodeMap);
        data.put("leader", leader);
        data.put("self", self);

        return data;
    }
}
