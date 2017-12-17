package com.robin.manager.config;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.model.UpdateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunInit implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(AppRunInit.class);

    @Value("${cluster.ip:}")
    private String clusterIp;

    @Value("${cluster.port:0}")
    private int clusterPort;

    @Value("${local.ip}")
    private String ip;

    @Value("${server.port}")
    private int port;

    @Value("${node.id}")
    private String nodeId;

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    public void run(String... args) {

        if (clusterIp == null || "".equals(clusterIp) || clusterPort == 0){
            brokerNodeManager.setSelfLeader();
            return;
        } else {
            try {
                // 加入新节点
                HttpRequest httpRequest = HttpRequest.post("http://".concat(clusterIp).concat(":").concat(String.valueOf(clusterPort)).concat("/core/add"))
                        .form("nodeId", nodeId)
                        .form("ip", ip)
                        .form("port", port);

                if (httpRequest.code() == 200) {
                    String body = httpRequest.body();
                    JSONObject data = JSONObject.parseObject(body).getJSONObject("data");

                    UpdateData updateData = new UpdateData(data);
                    brokerNodeManager.refreshData(updateData);

                    logger.info("加入集群成功");
                    logger.info("leader: {}", updateData.getLeader());

                } else {
                    logger.warn("加入集群节点失败");
                }
            } catch (Exception e) {
                logger.warn("加入集群节点失败", e);
                brokerNodeManager.setSelfLeader();
            }
        }
    }
}
