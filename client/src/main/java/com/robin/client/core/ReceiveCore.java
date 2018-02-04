package com.robin.client.core;

import com.robin.base.core.Connections;
import com.robin.base.core.ProtrolCore;
import com.robin.base.module.FlexibleData;
import com.robin.client.ReceiveListener;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiveCore {

    final static private ReceiveCore instance = new ReceiveCore();

    private Connections connections;

    final private List<NetSocket> netSocketList;

    private Map<String, ReceiveListener> listenerMaps;

//    private final ProtrolCore protrolCore = new ProtrolCore();
    private Map<String, ProtrolCore> protrolCoreMap;

    private final LinkedBlockingQueue<FlexibleData> flexibleDataList;


    private Vertx vertx;

    private ReceiveCore() {
        this.netSocketList = new ArrayList<>();
        this.vertx = Vertx.vertx();
        this.listenerMaps = new ConcurrentHashMap<>();
        this.flexibleDataList = new LinkedBlockingQueue<>();
        this.protrolCoreMap = new ConcurrentHashMap<>();

        new Thread(new Run()).start();
    }

    public static ReceiveCore getInstance() {
        return instance;
    }

    public void addConnection(String uri) {
        this.connections = new Connections(uri);
        this.initNetSocket();
    }

    public void addListener(ReceiveListener receiveListener) {
        this.listenerMaps.put(receiveListener.topic(), receiveListener);
        this.handshake(receiveListener);
    }

    private void handshake(ReceiveListener receiveListener) {
        String topic = receiveListener.topic();
        FlexibleData flexibleData = new FlexibleData(topic, FlexibleData.HAND_SHAKE, FlexibleData.RECEIVE_TYPE);

        ProtrolCore protrolCore = protrolCoreMap.getOrDefault(topic, new ProtrolCore());
        protrolCoreMap.put(topic, protrolCore);

        int index = topic.hashCode() % netSocketList.size();
        netSocketList.get(index).write(flexibleData.pack());
        netSocketList.get(index).handler(res->{
            List<FlexibleData> dataList = protrolCore.parseFlexibleDatas(res);
            flexibleDataList.addAll(dataList);
        });
    }


    private void initNetSocket() {
        for (Connections.HostAndPort hostAndPort : this.connections.getcList()) {
            NetClient netClient = vertx.createNetClient();
            netClient.connect(hostAndPort.getPort(), hostAndPort.getHost(), res->{

                if (res.succeeded()) {
                    netSocketList.add(res.result());
                }
            });
        }
    }

    private class Run implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    FlexibleData data = flexibleDataList.take();
                    ReceiveListener receiveListener = listenerMaps.get(data.getTopic());
                    if (receiveListener != null) {
                        receiveListener.onReceive(data.getData().toJsonObject());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
