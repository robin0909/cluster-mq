package com.robin.manager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.model.TopicWrap;
import com.robin.manager.service.RemoteTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoteTopicServiceImpl implements RemoteTopicService {

    final static private String addTopicUrl = "/topic/create";
    final static private String removeTopicUrl = "/topic/remove";

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    public boolean addTopic(TopicWrap topicWrap) {

        HttpRequest httpRequest = HttpRequest.post(brokerNodeManager.getBrokerNode(brokerNodeManager.getLeader()).url().concat(addTopicUrl))
                .form("topic", topicWrap.getTopic())
                .form("type", topicWrap.getType());

        if (httpRequest.code() == 200) {
            int code = JSONObject.parseObject(httpRequest.body()).getInteger("code");
            if (code == 200) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public TopicWrap removeTopic(String topic) {

        HttpRequest httpRequest = HttpRequest.post(brokerNodeManager.getBrokerNode(brokerNodeManager.getLeader()).url().concat(removeTopicUrl))
                .form("topic", topic);

        if (httpRequest.code() == 200) {
            JSONObject json = JSONObject.parseObject(httpRequest.body());

            if (json.getInteger("code") == 200) {
                return json.getObject("data", TopicWrap.class);
            }
        }

        return null;
    }
}
