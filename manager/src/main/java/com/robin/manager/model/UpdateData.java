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

    public UpdateData() {
    }

    public UpdateData(Map<String, BrokerNode> brokerNodeMap, String leader, String self) {
        this.brokerNodeMap = brokerNodeMap;
        this.leader = leader;
        this.self = self;
    }

    public UpdateData(JSONObject json) {

        Map<String, JSONObject> tempMap = json.getObject("brokerNodeMap", Map.class);

        Map<String, BrokerNode> stringBrokerNodeHashMap = new ConcurrentHashMap<String, BrokerNode>();

        Set<Map.Entry<String, JSONObject>> entries = tempMap.entrySet();
        Iterator<Map.Entry<String, JSONObject>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JSONObject> entry = iterator.next();
            stringBrokerNodeHashMap.put(entry.getKey(), new BrokerNode(entry.getValue()));
        }

        this.leader = json.getString("leader");
        this.self = json.getString("self");
        this.brokerNodeMap = stringBrokerNodeHashMap;
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
