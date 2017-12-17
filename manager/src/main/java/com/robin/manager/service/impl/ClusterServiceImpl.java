package com.robin.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.model.BrokerNode;
import com.robin.manager.model.UpdateData;
import com.robin.manager.service.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClusterServiceImpl implements ClusterService {

    final static private Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);

    final static private String addUrl = "/core/add";
    final static private String deleteNodeUrl = "/node/delete";
    final static private String resetUrl = "/core/reset";

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    public UpdateData addBrokerNode(BrokerNode brokerNode) {

        HttpRequest httpRequest = HttpRequest.post(brokerNodeManager.getBrokerNode(brokerNodeManager.getLeader()).url().concat(addUrl))
                .form("nodeId", brokerNode.getId())
                .form("ip", brokerNode.getIp())
                .form("port", brokerNode.getPort())
                .connectTimeout(500)
                .readTimeout(500);

        if (httpRequest.code() == 200) {
            String body = httpRequest.body();
            JSONObject data = JSONObject.parseObject(body).getJSONObject("data");
            return new UpdateData(data);
        } else {
            return null;
        }
    }

    public UpdateData removeBrokerNode(String nodeId) {

        HttpRequest httpRequest = HttpRequest.post(brokerNodeManager.getBrokerNode(brokerNodeManager.getLeader()).url().concat(deleteNodeUrl))
                .form("nodeId", nodeId)
                .connectTimeout(500)
                .readTimeout(500);

        if (httpRequest.code() == 200) {
            String body = httpRequest.body();
            JSONObject data = JSONObject.parseObject(body).getJSONObject("data");
            return new UpdateData(data);
        }

        return null;
    }

    public boolean resetRelativeNode(UpdateData updateData, String url) {

        HttpRequest httpRequest = HttpRequest.post(url.concat(resetUrl))
                .form("json", updateData.toJson())
                .connectTimeout(500)
                .readTimeout(500);

        if (httpRequest.code() == 200) {
            String body = httpRequest.body();
            JSONObject jsonObject = JSONObject.parseObject(body);
            Integer code = jsonObject.getInteger("code");
            if (code == 200) {
                return true;
            }
            logger.warn("重置失败, msg: {}", jsonObject.getString("msg"));
        }
        return false;
    }

}
