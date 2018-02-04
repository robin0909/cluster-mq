package com.robin.client;

import com.robin.client.core.ReceiveCore;

public class Receiver {


    private String uri;
    private ReceiveCore receiveCore;


    private Receiver() {
        this.receiveCore = ReceiveCore.getInstance();
    }

    public static Receiver init(String uri) {
        Receiver receiver = new Receiver();
        receiver.uri = uri;

        receiver.receiveCore.addConnection(receiver.uri);
        return receiver;
    }

    public void addListener(ReceiveListener receiveListener) {
        this.receiveCore.addListener(receiveListener);
    }
}
