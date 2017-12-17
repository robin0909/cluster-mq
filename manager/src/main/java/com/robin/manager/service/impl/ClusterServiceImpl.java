package com.robin.manager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.model.BrokerNode;
import com.robin.manager.model.UpdateData;
import com.robin.manager.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClusterServiceImpl implements ClusterService {

    final static private String addUrl = "/core/add";

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    public UpdateData addBrokerNode(BrokerNode brokerNode) {

        HttpRequest httpRequest = HttpRequest.post(brokerNodeManager.getBrokerNode(brokerNodeManager.getLeader()).url().concat(addUrl))
                .form("nodeId", brokerNode.getId())
                .form("ip", brokerNode.getIp())
                .form("port", brokerNode.getPort())
                .connectTimeout(500);

        if (httpRequest.code() == 200) {
            String body = httpRequest.body();
            JSONObject data = JSONObject.parseObject(body).getJSONObject("data");
            return new UpdateData(data);
        } else {
            return null;
        }
    }

    public boolean removeBrokerNode(String id) {

        return false;
    }

}
