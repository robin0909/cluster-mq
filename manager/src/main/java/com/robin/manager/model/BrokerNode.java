package com.robin.manager.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 集群节点实体类
 */
public class BrokerNode {

    // 节点唯一 id(每个节点在一段时间生成一个有效的 uuid)
    final private String id;

    // 节点 ip
    final private String ip;

    // 端口（tcp加入集群监听端口）
    final private int port;

    public BrokerNode(String id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public BrokerNode(JSONObject json) {
        this.id = json.getString("id");
        this.ip = json.getString("ip");
        this.port = json.getInteger("port");
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String url() {
        StringBuffer url = new StringBuffer();
        url.append("http://").append(ip).append(":").append(port);

        return url.toString();
    }
}
