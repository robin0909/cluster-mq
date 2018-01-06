package com.robin.manager.controller;

import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.help.action.Result;
import com.robin.manager.model.TopicWrap;
import com.robin.manager.service.RemoteTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * topic 相关管理
 */
@RestController
@RequestMapping(value = "/topic")
public class TopicController {

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    @Autowired
    private RemoteTopicService remoteTopicService;

    /**
     *  创建一个topic
     * @param topic
     *          name
     * @param type
     *          类型 0.一对一  1.一对多
     * @return
     */
    @RequestMapping(value = "/create")
    public Result createTopic(String topic, int type) {

        boolean isAdd;

        if (brokerNodeManager.isLeader()) {
            isAdd = brokerNodeManager.addTopic(new TopicWrap(topic, type));
        } else {
            isAdd = remoteTopicService.addTopic(new TopicWrap(topic, type));

            // 触发一次更新操作，让当前的节点都从主节点同步最新的topic
            brokerNodeManager.updateTopic();
        }

        if (isAdd) {
            return Result.newSucess();
        } else {
            return Result.newFail();
        }
    }

    @RequestMapping(value = "/remove")
    public Result removeTopic(String topic) {
        TopicWrap topicWrap;

        if (brokerNodeManager.isLeader()) {
            topicWrap = brokerNodeManager.removeTopic(topic);
        } else {
            topicWrap = remoteTopicService.removeTopic(topic);

            // 触发一次更新操作，让当前的节点都从主节点同步最新的topic
            brokerNodeManager.updateTopic();
        }

        if (topicWrap != null) {
            return Result.newSucess(topicWrap);
        } else {
            return Result.newFail();
        }
    }

    @RequestMapping(value = "/get/all")
    public Result getAll() {

        Map<String, TopicWrap> topicWrapMap = brokerNodeManager.getTopicWrapMap();

        return Result.newSucess(topicWrapMap);
    }

}
