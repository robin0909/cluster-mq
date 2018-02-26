package com.robin.base.module;

import com.robin.base.type.SubScribeType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 核心协议包
 *  |-版本|订阅类型|topic长度|topic| STYPE | 数据包
 *
 *  |--type--|--sType--|--TLEN--|--TOPIC--|
 *
 *  |--type--|--sType--|--TLEN--|--TOPIC--|--DLEN--|--DATA--|
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

    /**-------------------**/

    /**
     * 发送方
     */
    final static public short SEND_TYPE = 1;

    /**
     * 接收方
     */
    final static public short RECEIVE_TYPE = 1;

    private short subsribeType;

    /**
     * 包里面的总长度 byte
     */
    private int len = 0;

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
    public FlexibleData(String topic, short protocolType, short subsribeType) {
        this.header = Buffer.buffer();

        this.header.appendShort(protocolType);
        this.header.appendShort(subsribeType);
        this.header.appendInt(topic.length());
        this.header.appendString(topic);

        this.topic = topic;
        this.protocolType = protocolType;
        this.subsribeType = subsribeType;

        this.protocolType = HAND_SHAKE;
    }

    public FlexibleData(String topic, SubScribeType type, short subsribeType, Buffer data) {

        this.protocolType = DATA;

        this.subsribeType = subsribeType;

        this.header = this.generateHeader(topic, data.length());

        this.topic = topic;

        this.type = type;

        this.data = data;

    }

    public FlexibleData(Buffer buffer) {
        this.parseHeaderAndData(buffer, 0);
    }

    public FlexibleData(Buffer buffer, int start) {
        this.parseHeaderAndData(buffer, start);
    }

    private void parseHeaderAndData(Buffer buffer, int start) {

        int len = start;

        this.protocolType = buffer.getShort(len);
        len += Short.BYTES;

        this.subsribeType = buffer.getShort(len);
        len += Short.BYTES;

        int tLen = buffer.getInt(len);
        len += Integer.BYTES;

        this.topic = buffer.getString(len, len+tLen);
        len += tLen;

        if (protocolType == HAND_SHAKE || protocolType == ACK) {
            this.len = len;
            return;
        }

        int dLen = buffer.getInt(len);
        len += Integer.BYTES;

        if (len + dLen > buffer.length()) {
            throw new IndexOutOfBoundsException();
        }

        this.data = buffer.getBuffer(len, len + dLen);

        this.header = this.generateHeader(topic, dLen);

        this.len = len + dLen - start;
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
        if (data != null && data.length() != 0) {//data是数据包
            buffer.appendBuffer(this.data);
        }

        return buffer;
    }

    /**
     *  Header 生成
     * @param topic
     * @return
     */
    private Buffer generateHeader(String topic, int dLen) {
        Buffer buffer = Buffer.buffer();

        buffer.appendShort(this.protocolType);//包类型，是握手包还是数据包还是回复包
        buffer.appendShort(this.subsribeType);//发送类型是一对一或者一对多
        buffer.appendInt(topic.length());
        buffer.appendString(topic);
        buffer.appendInt(dLen);

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

    public int getLen() {
        return this.len;
    }

    public short getSubsribeType() {
        return subsribeType;
    }

    public void setSubsribeType(short subsribeType) {
        this.subsribeType = subsribeType;
    }
}
