package com.robin.manager.config;

import com.robin.manager.core.BrokerNodeManager;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class Config {

    @Bean
    public BrokerNodeManager brokerNodeManager() {
        BrokerNodeManager brokerNodeManager = new BrokerNodeManager();
        return brokerNodeManager;
    }
}
