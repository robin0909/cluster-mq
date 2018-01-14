package com.robin.manager.config;

import com.robin.manager.core.BrokerNodeManager;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
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

    @Value("${tcp.port}")
    private int tcpPort;

    @Bean
    public BrokerNodeManager brokerNodeManager() {
        BrokerNodeManager brokerNodeManager = new BrokerNodeManager();
        brokerNodeManager.init(nodeId, ip, port);
        return brokerNodeManager;
    }

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public NetServerOptions netServerOptions() {
        NetServerOptions netServerOptions = new NetServerOptions();

        netServerOptions.setPort(tcpPort);

        return netServerOptions;
    }

}
