package com.robin.manager.controller;

import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.help.action.Result;
import com.robin.manager.model.BrokerNode;
import com.robin.manager.model.UpdateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * core 内部核心借口，对外不开放
 *
 * TODO 完善，加入 内部token 禁止外部访问
 */
@RestController
@RequestMapping(value = "/core")
public class CoreController {

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    /**
     * 接茬非 leader 节点是否可用
     * @return
     */
    @RequestMapping(value = "/check")
    public Result check() {
        return Result.newSucess();
    }

    /**
     * 集群中的非 leader 节点向 leader节点进行同步数据
     * @param data
     * @return
     */
    @RequestMapping(value = "/update")
    public Result update(String data) {

        // todo data 暂时没用，以后扩展选举算法可用到

        UpdateData updateData = brokerNodeManager.getbrokerNodeMap();

        return Result.newSucess(updateData);
    }

    /**
     *  当一个新的节点想加入集群的话，调用该接口进行加入
     * @param nodeId
     *      id
     * @param ip
     *      对方 ip
     * @param port
     *      port
     * @return
     *      返回最新数据 UpdateData
     */
    @RequestMapping(value = "/add")
    public Result addCluster(String nodeId, String ip, int port) {

        BrokerNode brokerNode = new BrokerNode(nodeId, ip, port);

        brokerNodeManager.upsertBrokerNode(brokerNode);

        UpdateData updateData = brokerNodeManager.getbrokerNodeMap();

        return Result.newSucess(updateData);
    }
}
