package com.robin.manager.config;

import com.robin.manager.core.BrokerNodeManager;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Value("${local.ip}")
    private String ip;

    @Value("${server.port}")
    private int port;

    @Value("${node.id}")
    private String nodeId;

    @Bean
    public BrokerNodeManager brokerNodeManager() {
        BrokerNodeManager brokerNodeManager = new BrokerNodeManager();
        brokerNodeManager.init(nodeId, ip, port);
        return brokerNodeManager;
    }
}
