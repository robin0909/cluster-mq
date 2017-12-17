package com.robin.manager.service;

import com.robin.manager.model.BrokerNode;
import com.robin.manager.model.UpdateData;

public interface ClusterService {

    /**
     *  添加集群节点
     * @return
     *      true 添加成功
     *      false 添加失败
     */
    UpdateData addBrokerNode(BrokerNode brokerNode);

    /**
     *  移除节点
     * @param nodeId
     * @return
     */
    UpdateData removeBrokerNode(String nodeId);

    boolean resetRelativeNode(UpdateData updateData, String url);
}
