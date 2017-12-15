package com.robin.manager.core;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.robin.manager.model.BrokerNode;
import com.robin.manager.model.UpdateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 *  brokerNode 集中管理
 */
public class BrokerNodeManager {

    private final static Logger logger = LoggerFactory.getLogger(BrokerNodeManager.class);

    final Object mux = new Object();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    private final AbstractMap<String, BrokerNode> brokerNodeMap = new ConcurrentHashMap<String, BrokerNode>();

    private final String checkUrl = "/core/check";
    private final String updateUrl = "/core/update";

    /**
     * leader 对应的 id
     */
    private String leader;

    /**
     * 自己的 id
     */
    private String self;

    /**
     *  自己的节点
     */
    private BrokerNode selfBrokerNode;



    /**
     * 初始化定时任务， 必须要调用
     *  500 ms
     */
    public void init(String ip, int port) {

        executor.scheduleWithFixedDelay(new UpdateFromLeaderNode(), 600, 600, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(new CheckNodeTask(), 600, 600, TimeUnit.MILLISECONDS);

        this.self = UUID.randomUUID().toString();
        this.selfBrokerNode = new BrokerNode(this.self, ip, port);

        this.upsertBrokerNode(this.selfBrokerNode);
    }

    public BrokerNode upsertBrokerNode(BrokerNode brokerNode) {
        synchronized (mux) {
            String id = brokerNode.getId();
            return brokerNodeMap.put(id, brokerNode);
        }
    }

    public BrokerNode removeBrokerNode(String id) {
        synchronized (mux) {
            return brokerNodeMap.remove(id);
        }
    }

    public UpdateData getbrokerNodeMap() {

        UpdateData updateData = new UpdateData();
        synchronized (mux) {
            Set<Map.Entry<String, BrokerNode>> entries = brokerNodeMap.entrySet();

            HashMap<String, BrokerNode> map = new HashMap<String, BrokerNode>();
            for (Map.Entry<String, BrokerNode> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            updateData.setBrokerNodeMap(map);
        }

        updateData.setLeader(leader);
        updateData.setSelf(self);

        return updateData;
    }

    /**
     * 参加竞选 leader
     */
    private void electLeader() {

        int nodeSize = brokerNodeMap.size();
        if (nodeSize < 2) {
            logger.warn("节点数量小于 2 个, 数据可能会丢失");
        } else {
            Set<Map.Entry<String, BrokerNode>> entries = brokerNodeMap.entrySet();
            Iterator<Map.Entry<String, BrokerNode>> iterator = entries.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, BrokerNode> entry = iterator.next();
                this.leader = entry.getKey();

                logger.info("-------------new leader-------------");
                logger.info("id: {}, ip: {}:{}", leader, entry.getValue().getIp(), entry.getValue().getPort());

                break;
            }


        }
    }

    public boolean isLeader() {
        return this.self.equals(this.leader);
    }

    public boolean existsLeader() {
        return this.leader != null && !"".equals(leader) && brokerNodeMap.containsKey(leader);
    }


    /**
     *  只有 leader 才会进行检查，检查所有的节点；一旦发现无效节点就移除
     */
    private class CheckNodeTask implements Runnable {

        public void run() {
            if (BrokerNodeManager.this.isLeader()) {

                Set<Map.Entry<String, BrokerNode>> entries = brokerNodeMap.entrySet();
                Iterator<Map.Entry<String, BrokerNode>> iterator = entries.iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, BrokerNode> nodeEntry = iterator.next();
                    if (!nodeEntry.getKey().equals(BrokerNodeManager.this.self)) {
                        // 检查节点是否可用
                        boolean isNormal = BrokerNodeManager.this.check(nodeEntry.getValue());
                        // 移除不可用节点
                        if (!isNormal) {
                            BrokerNodeManager.this.removeBrokerNode(nodeEntry.getKey());
                        }
                    }
                }
            }
        }
    }

    private boolean check(BrokerNode brokerNode) {

        String url = brokerNode.url();
        HttpRequest httpRequest = HttpRequest.post(url.concat(this.checkUrl));

        if (httpRequest.code() == 200) {
            return true;
        }

        return false;
    }

    /**
     * 从 leader 节点进行同步，保证数据的一致性，一旦无法进行同步，那么leader 就是失效了，需要从新进行选举
     *
     * 同步规则，每次从 leader 节点拉取数据
     */
    private class UpdateFromLeaderNode implements Runnable {

        public void run() {

            if (!BrokerNodeManager.this.isLeader()) {
                JSONObject param = new JSONObject();
                param.put("leader", leader);
                param.put("self", self);

                BrokerNode brokerNode = brokerNodeMap.get(leader);

                HttpRequest httpRequest = HttpRequest.post(brokerNode.url().concat(updateUrl))
                        .form("data", param.toJSONString())
                        .connectTimeout(300);

                if (httpRequest.code() == 200) {

                    String body = httpRequest.body();
                    JSONObject data = JSONObject.parseObject(body);
                    UpdateData updateData = new UpdateData(data);
                    BrokerNodeManager.this.refreshData(updateData);

                } else {
                    BrokerNodeManager.this.removeBrokerNode(leader);

                    // 开始选举
                    BrokerNodeManager.this.electLeader();
                }
            }
        }
    }

    private void refreshData(UpdateData updateData) {
        //todo 更新 map 和 leader数据
    }


}
