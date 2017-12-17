package com.robin.manager.controller;

import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.help.action.Result;
import com.robin.manager.model.UpdateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 节点相关 api
 */
@RestController
@RequestMapping(value = "/node")
public class BrokerNodeController {

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    @RequestMapping(value = "/all")
    public Result getAllBrokerNode() {

        UpdateData updateData = brokerNodeManager.getbrokerNodeMap();

        return Result.newSucess(updateData);
    }
}
