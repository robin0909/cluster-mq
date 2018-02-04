package com.robin.client;

import com.robin.base.module.FlexibleData;
import com.robin.base.type.SubScribeType;
import com.robin.client.core.SendCore;
import io.vertx.core.json.JsonObject;

public class Sender {

    /**
     *  127.0.0.1:8080,127.0.0.1.8081
     */
    private String uri;

    /**
     * topic
     */
    private String topic;

    private SendCore sendCore;

    private Sender() {
        this.sendCore = SendCore.getInstance();
    }

    public static Sender init(String uri, String topic) {
        Sender sender = new Sender();
        sender.uri = uri;
        sender.topic = topic;

        sender.sendCore.addConnection(sender.uri);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 握手包
        sender.sendCore.send(new FlexibleData(sender.topic, FlexibleData.HAND_SHAKE, FlexibleData.SEND_TYPE));
        return sender;
    }


    public void send(JsonObject data, SubScribeType type) {

        FlexibleData flexibleData = new FlexibleData(this.topic, type, FlexibleData.SEND_TYPE, data.toBuffer());

        sendCore.send(flexibleData);
    }

}
