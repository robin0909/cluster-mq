package com.robin.manager.core;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.robin.manager.model.BrokerNode;
import com.robin.manager.model.TopicWrap;
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

    final Object nodeMux = new Object();
    final Object topicMux = new Object();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);

    private final AbstractMap<String, BrokerNode> brokerNodeMap = new ConcurrentHashMap<String, BrokerNode>();

    private final ConcurrentHashMap<String, TopicWrap> topicWrapMap = new ConcurrentHashMap<String, TopicWrap>();

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


    private String ip;
    private int port;



    /**
     * 初始化定时任务， 必须要调用
     *  500 ms
     */
    public void init(String nodeId , String ip, int port) {

        executor.scheduleWithFixedDelay(new UpdateFromLeaderNode(), 300, 1000, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(new CheckNodeTask(), 600, 1500, TimeUnit.MILLISECONDS);

        this.self = nodeId;
        this.selfBrokerNode = new BrokerNode(this.self, ip, port);

        this.upsertBrokerNode(this.selfBrokerNode);

        this.ip = ip;
        this.port = port;
    }

    public BrokerNode upsertBrokerNode(BrokerNode brokerNode) {
        synchronized (nodeMux) {
            String id = brokerNode.getId();
            return brokerNodeMap.put(id, brokerNode);
        }
    }

    public BrokerNode removeBrokerNode(String id) {
        synchronized (nodeMux) {
            return brokerNodeMap.remove(id);
        }
    }

    public BrokerNode getBrokerNode(String id) {
        synchronized (nodeMux) {
            return brokerNodeMap.get(id);
        }
    }

    public boolean addTopic(TopicWrap topicWrap) {
        synchronized (topicMux) {
            return topicWrapMap.put(topicWrap.getTopic(), topicWrap) != null;
        }
    }

    public TopicWrap removeTopic(String topic) {
        synchronized (topicMux) {
            return topicWrapMap.remove(topic);
        }
    }

    public void clean() {

        synchronized (nodeMux) {
            this.leader = null;
            this.brokerNodeMap.clear();
        }
    }

    public String getLeader() {
        return leader;
    }

    public UpdateData getBrokerUpdateData() {

        UpdateData updateData = new UpdateData();

        updateData.setBrokerNodeMap(this.getBrokerNodeMap());
        updateData.setTopicWrapMap(this.getTopicWrapMap());

        updateData.setLeader(leader);
        updateData.setSelf(self);

        return updateData;
    }

    public Map<String, BrokerNode> getBrokerNodeMap() {
        synchronized (nodeMux) {
            Set<Map.Entry<String, BrokerNode>> entries = brokerNodeMap.entrySet();

            HashMap<String, BrokerNode> map = new HashMap<String, BrokerNode>();
            for (Map.Entry<String, BrokerNode> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }


    public synchronized Map<String, TopicWrap> getTopicWrapMap() {

        synchronized (topicMux) {
            HashMap<String, TopicWrap> map = new HashMap<>();
            Set<Map.Entry<String, TopicWrap>> entries = topicWrapMap.entrySet();

            for (Map.Entry<String, TopicWrap> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }

            return map;
        }
    }

    /**
     * 参加竞选 leader
     */
    private void electLeader() {

        synchronized (nodeMux) {
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

    /**
     * 当竞选失败时，就选择自己为 leader
     */
    public void setSelfLeader() {
        this.leader = this.self;

        logger.info("-------------new leader-------------");
        logger.info("id: {}, ip: {}:{}", leader, ip, port);
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

            try {
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
            } catch (Throwable e) {
                logger.error("error", e);
            }

        }
    }

    private boolean check(BrokerNode brokerNode) {

        String url = brokerNode.url();
        HttpRequest httpRequest = null;
        try {
            httpRequest = HttpRequest.post(url.concat(this.checkUrl)).connectTimeout(300);
            if (httpRequest != null && httpRequest.code() == 200) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("node is down");
            logger.warn(url);
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

            try {
                if (BrokerNodeManager.this.existsLeader()) {
                    if (!BrokerNodeManager.this.isLeader()) {

                        JSONObject param = new JSONObject();
                        param.put("leader", leader);
                        param.put("self", self);

                        BrokerNode brokerNode = BrokerNodeManager.this.getBrokerNode(leader);

                        try {
                            HttpRequest httpRequest = HttpRequest
                                    .post(brokerNode.url().concat(updateUrl))
                                    .connectTimeout(500)
                                    .readTimeout(500)
                                    .form("data", param.toJSONString());

                            if (httpRequest != null && httpRequest.code() == 200) {

                                String body = httpRequest.body();
                                JSONObject data = JSONObject.parseObject(body).getJSONObject("data");

                                logger.info("data: {}", data.toJSONString());

                                UpdateData updateData = new UpdateData(data);
                                BrokerNodeManager.this.refreshData(updateData);

                            } else {
                                BrokerNodeManager.this.removeBrokerNode(BrokerNodeManager.this.leader);
                                BrokerNodeManager.this.leader = null;

                                // 开始选举
                                BrokerNodeManager.this.electLeader();
                            }
                        } catch (Exception e) {
                            logger.warn("leader is down");
                            BrokerNodeManager.this.removeBrokerNode(BrokerNodeManager.this.leader);
                            BrokerNodeManager.this.leader = null;

                            // 开始选举
                            BrokerNodeManager.this.electLeader();
                        }
                    }
                }
            } catch (Throwable e) {
                logger.error("error", e);
            }

        }
    }

    public void updateTopic() {
        executor.execute(new UpdateFromLeaderNode());
    }

    public void refreshData(UpdateData updateData) {
        // 更新 map 和 leader数据
        synchronized (nodeMux) {
            brokerNodeMap.clear();
            brokerNodeMap.putAll(updateData.getBrokerNodeMap());
            topicWrapMap.putAll(updateData.getTopicWrapMap());
            this.leader = updateData.getLeader();
        }
    }


}
