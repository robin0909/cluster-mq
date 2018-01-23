package com.robin.manager.core;

import com.robin.base.module.FlexibleData;
import com.robin.manager.server.CoreServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.concurrent.LinkedBlockingQueue;

@Configurable
public class SingleQueue implements Runnable {

    final static private Logger logger = LoggerFactory.getLogger(SingleQueue.class);

    LinkedBlockingQueue<FlexibleData> datas;

    private CoreServer coreServer;

    SingleQueue(CoreServer coreServer) {
        this.datas = new LinkedBlockingQueue();
        this.coreServer = coreServer;
    }

    public void addData(FlexibleData flexibleData) {
        datas.add(flexibleData);
        logger.info("add data");
    }

    @Override
    public void run() {
        while (true) {
            try {
                FlexibleData flexibleData = datas.take();

                logger.info("get data will send");

                coreServer.send(flexibleData);
            } catch (Throwable e) {
                logger.error("error", e);
                e.printStackTrace();
            }
        }
    }
}
