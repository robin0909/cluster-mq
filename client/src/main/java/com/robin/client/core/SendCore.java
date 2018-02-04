package com.robin.client.core;


import com.robin.base.core.Connections;
import com.robin.base.module.FlexibleData;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.ArrayList;
import java.util.List;

public class SendCore {

    private Connections connections;

    final private List<NetSocket> netSocketList;

    private Vertx vertx;

    final static private SendCore instance = new SendCore();

    private SendCore () {
        this.vertx = Vertx.vertx();
        this.netSocketList = new ArrayList<>();
    }

    public static SendCore getInstance() {
        return instance;
    }

    public void addConnection(String uri) {
        this.connections = new Connections(uri);
        this.initNetSocket();
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

    public void send(FlexibleData data) {
        int index = data.getTopic().hashCode() % netSocketList.size();
        netSocketList.get(index).write(data.pack());
    }

}
