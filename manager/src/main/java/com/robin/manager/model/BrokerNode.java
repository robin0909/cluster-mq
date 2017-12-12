package com.robin.manager.model;

/**
 * 集群节点实体类
 */
public class BrokerNode {

    // 节点唯一 id(每个节点在一段时间生成一个有效的 uuid)
    private String id;

    // 节点 ip
    private String ip;

    // 端口（tcp加入集群监听端口）
    private int port;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
