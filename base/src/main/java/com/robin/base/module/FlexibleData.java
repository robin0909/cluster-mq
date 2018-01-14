package com.robin.base.module;

import com.robin.base.type.SubScribeType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;

/**
 * 核心协议包
 *  |-版本|topic长度|topic| STYPE | 数据包
 *
 *  |--type--|--TLEN--|--TOPIC--|
 *
 *  |--type--|--TLEN--|--TOPIC--|--DLEN--|--DATA--|
 */
public class FlexibleData {

    /**
     *  协议包类型  握手包
     */
    final static public short HAND_SHAKE = 1;

    /**
     *  协议包类型  数据包
     */
    final static public short DATA = 2;

    /**
     *  协议包类型  回复包
     */
    final static public short ACK = 3;

    private short protocolType;

    /**
     * 协议头
     */
    private Buffer header;

    private String topic;

    private SubScribeType type;

    /**
     * 数据包
     */
    private Buffer data;

    /**
     * 握手包
     * @param topic
     */
    public FlexibleData(String topic, short protocolType) {
        this.header = Buffer.buffer();

        this.header.appendShort(protocolType);
        this.header.appendInt(topic.length());
        this.header.appendString(topic);

        this.protocolType = HAND_SHAKE;
    }

    public FlexibleData(String topic, SubScribeType type, Buffer data) {

        this.protocolType = DATA;

        this.header = this.generateHeader(topic, data.length());

        this.topic = topic;

        this.type = type;

        this.data = data;
    }

    public FlexibleData(Buffer buffer) {
        this.parseHeaderAndData(buffer);
    }

    private void parseHeaderAndData(Buffer buffer) {

        int len = 0;

        this.protocolType = buffer.getShort(len);
        len += Short.BYTES;

        int tLen = buffer.getInt(len);
        len += Integer.BYTES;

        this.topic = buffer.getString(len, len+tLen);
        len += tLen;

        if (protocolType == HAND_SHAKE || protocolType == ACK) {
            return;
        }

        long dLen = buffer.getLong(len);
        len += Long.BYTES;

//        this.type = type;

        this.data = buffer.getBuffer(len, buffer.length());

        this.header = this.generateHeader(topic, dLen);
    }

    public SubScribeType getType() {
        return type;
    }

    public void setType(SubScribeType type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    /**
     * 组装最后要发送的数据
     * @return
     */
    public Buffer pack() {
        Buffer buffer = Buffer.buffer();

        buffer.appendBuffer(this.header);
        if (data != null && data.length() != 0) {
            buffer.appendBuffer(this.data);
        }

        return buffer;
    }

    /**
     *  Header 生成
     * @param topic
     * @return
     */
    private Buffer generateHeader(String topic, long dLen) {
        Buffer buffer = Buffer.buffer();

        buffer.appendShort(this.protocolType);
        buffer.appendInt(topic.length());
        buffer.appendString(topic);
        buffer.appendLong(dLen);

        return buffer;
    }

    public Buffer getData() {
        return this.data;
    }

    public boolean isHandlShake() {
        return this.protocolType == HAND_SHAKE;
    }

    public boolean isData() {
        return this.protocolType == DATA;
    }

    public boolean isACK() {
        return this.protocolType == ACK;
    }


    public static void main(String[] args) {

        JsonObject temp = new JsonObject();
        temp.put("name", "robin");
        temp.put("age", 25);
        Buffer data = Buffer.buffer(temp.toString().getBytes());

        FlexibleData flexibleData = new FlexibleData("demo", SubScribeType.ONE_TO_ONE, data);

        FlexibleData flexibleData1 = new FlexibleData(flexibleData.pack());
//        flexibleData1.setType(SubScribeType.ONE_TO_ONE);

        System.out.println(flexibleData1.topic);

        System.out.println(flexibleData1.isData());

        System.out.println(new String(flexibleData1.getData().getBytes(), Charset.forName("UTF-8")));
    }

}
