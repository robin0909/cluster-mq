package com.robin.manager.controller;

import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.help.action.Result;
import com.robin.manager.model.BrokerNode;
import com.robin.manager.model.TopicWrap;
import com.robin.manager.model.UpdateData;
import com.robin.manager.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * 节点相关 api
 *
 * 进行的一系列的 update 操作都是在leader上进行的操作的
 */
@RestController
@RequestMapping(value = "/node")
public class BrokerNodeController {

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    @Autowired
    private ClusterService clusterService;

    @RequestMapping(value = "/all")
    public Result getAllBrokerNode() {

        UpdateData updateData = brokerNodeManager.getBrokerUpdateData();

        return Result.newSucess(updateData);
    }

    @RequestMapping(value = "/delete")
    public Result deleteBrokerNode(String nodeId) {

        if (brokerNodeManager.getLeader().equals(nodeId)) {
            return Result.newFail("删除leader节点无效", nodeId);
        }

        BrokerNode brokerNode = brokerNodeManager.getBrokerNode(nodeId);

        if (brokerNode == null) {
            return Result.newFail("不存在该节点", nodeId);
        }

        if (!brokerNodeManager.isLeader()) {
            clusterService.removeBrokerNode(nodeId);
        } else {
            brokerNodeManager.removeBrokerNode(nodeId);
        }

        Map<String, TopicWrap> topicWrapMap = brokerNodeManager.getTopicWrapMap();

        // 重置被删除的节点
        HashMap<String, BrokerNode> nodeHashMap = new HashMap<String, BrokerNode>();
        nodeHashMap.put(brokerNode.getId(), brokerNode);
        UpdateData updateData = new UpdateData(nodeHashMap, brokerNode.getId(), brokerNode.getId(), topicWrapMap);

        clusterService.resetRelativeNode(updateData, brokerNode.url());

        return Result.newSucess(brokerNodeManager.getBrokerUpdateData());
    }

    @RequestMapping(value = "/add")
    public Result addBrokerNode(String nodeId, String ip, int port) {

        BrokerNode brokerNode = new BrokerNode(nodeId, ip, port);

        if (!brokerNodeManager.isLeader()) {
            UpdateData updateData = clusterService.addBrokerNode(brokerNode);
            brokerNodeManager.refreshData(updateData);
        } else {
            brokerNodeManager.upsertBrokerNode(brokerNode);
        }

        UpdateData updateData = brokerNodeManager.getBrokerUpdateData();

        boolean isReset = clusterService.resetRelativeNode(updateData, brokerNode.url());

        if (isReset) {
            return Result.newSucess(updateData);
        } else {
            brokerNodeManager.removeBrokerNode(nodeId);
            return Result.newFail("添加节点失败", brokerNode);
        }
    }

}
