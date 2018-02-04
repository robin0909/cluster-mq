package com.robin.client;

import io.vertx.core.json.JsonObject;

public interface ReceiveListener {

    /**
     * topic
     * @return
     */
    String topic();

    /**
     * 接收数据
     * @param data
     */
    void onReceive(JsonObject data);

}
