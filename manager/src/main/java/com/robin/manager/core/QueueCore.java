package com.robin.manager.core;

import com.robin.base.module.FlexibleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class QueueCore {

    final static private Logger logger = LoggerFactory.getLogger(QueueCore.class);

    final private LinkedBlockingQueue<FlexibleData> queue = new LinkedBlockingQueue<>();

    final private Map<Integer, SingleQueue> hashSingleQueueMap = new ConcurrentHashMap<>();

    final private int threadCount = 10;
    final private ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    public QueueCore() {
        for (int i = 0; i < threadCount; i++) {
            hashSingleQueueMap.put(i, new SingleQueue());
        }
    }

    @PostConstruct
    public void init() {
        executor.execute(()->{
            try {
                FlexibleData flexibleData = queue.take();

                int hash = flexibleData.getTopic().hashCode() % threadCount;

                SingleQueue singleQueue = hashSingleQueueMap.get(hash);

                singleQueue.addData(flexibleData);

            } catch (Throwable e) {
                logger.error("error", e);
            }
        });
    }

    public void addData(FlexibleData flexibleData) {
        queue.add(flexibleData);
        logger.info("------------------");
        logger.info("accept data, topic: {}, data: {}", flexibleData.getTopic(), new String(flexibleData.getData().getBytes()));
    }



}
