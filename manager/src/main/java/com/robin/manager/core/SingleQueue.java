package com.robin.manager.core;

import com.robin.base.module.FlexibleData;
import com.robin.manager.server.CoreServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.concurrent.LinkedBlockingQueue;

@Configurable
public class SingleQueue extends Thread {

    final static private Logger logger = LoggerFactory.getLogger(SingleQueue.class);

    LinkedBlockingQueue<FlexibleData> datas;

    @Autowired
    private CoreServer coreServer;

    SingleQueue() {
        this.datas = new LinkedBlockingQueue();
    }

    public void addData(FlexibleData flexibleData) {
        datas.add(flexibleData);
    }

    @Override
    public void run() {
        try {
            FlexibleData flexibleData = datas.take();
            coreServer.send(flexibleData);
        } catch (Throwable e) {
            logger.error("error", e);
        }
    }
}
