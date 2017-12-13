package com.robin.manager.core;

import com.robin.manager.model.BrokerNode;

import java.util.concurrent.*;

/**
 *  brokerNode 集中管理
 */
public class BrokerNodeManager {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    private final ConcurrentHashMap<String, BrokerNode> brokerNodeMap = new ConcurrentHashMap<String, BrokerNode>();

    /**
     * leader 对应的 id
     */
    private String leader;



    /**
     * 初始化定时任务， 必须要调用
     *  500 ms
     *  500 ms
     */
    public void init() {
        executor.scheduleWithFixedDelay(new CheckLeaderTask(), 500, 500, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(new UpdateToLeaderNode(), 500, 500, TimeUnit.MILLISECONDS);
    }

    final Object mux = new Object();

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

    /**
     * 参加竞选 leader
     */
    private void electLeader() {

    }

    /**
     * 向 leader 同步自己的数据
     */
    private void updateToLeader() {

    }

    /**
     *  检查 leader 是否有效，一旦发现是无效的 leader 的话就进行竞选
     */
    private class CheckLeaderTask implements Runnable {

        public void run() {

        }
    }

    /**
     * 向 leader 节点进行同步，保证数据的一致性
     */
    private class UpdateToLeaderNode implements Runnable {

        public void run() {

        }
    }

}
