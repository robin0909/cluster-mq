package com.robin.base.core;

import com.robin.base.module.FlexibleData;
import com.robin.base.type.SubScribeType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProtrolCore {

    private final static Logger logger = LoggerFactory.getLogger(ProtrolCore.class);

    private Buffer globBuffer = Buffer.buffer();

    /**
     * 解包
     * @return
     */
    public List<FlexibleData> parseFlexibleDatas(Buffer buffer) {
        List<FlexibleData> list = new ArrayList<>();

        synchronized (globBuffer) {
            globBuffer.appendBuffer(buffer);
        }


        buffer = globBuffer;

        int tempLen = 0;
        int size = buffer.length();
        while (tempLen < size) {
            try {
                synchronized (globBuffer) {
                    FlexibleData flexibleData = new FlexibleData(buffer, tempLen);
                    tempLen += flexibleData.getLen();
                    list.add(flexibleData);
//                    globBuffer = globBuffer.slice(flexibleData.getLen(), globBuffer.length());
                    globBuffer = Buffer.buffer(globBuffer.getBytes(flexibleData.getLen(), globBuffer.length()));
                }
            } catch (Exception e) {
                break;
            }
        }

        return list;
    }

    public static void main(String[] args) {

        JsonObject temp = new JsonObject();
        temp.put("name", "robin");
        temp.put("age", 25);
        Buffer data = Buffer.buffer(temp.toString().getBytes());

        FlexibleData flexibleData1 = new FlexibleData("demo10", SubScribeType.ONE_TO_ONE, FlexibleData.SEND_TYPE, data);
        FlexibleData flexibleData2 = new FlexibleData("demo2", SubScribeType.ONE_TO_ONE, FlexibleData.SEND_TYPE, data);

        Buffer buffer = Buffer.buffer();
        buffer.appendBuffer(flexibleData1.pack());
        buffer.appendBuffer(flexibleData2.pack());

        List<FlexibleData> flexibleDataList = new ProtrolCore().parseFlexibleDatas(buffer);

        for (FlexibleData flexibleData : flexibleDataList) {
            System.out.println("-----------------");
            System.out.println(flexibleData.getLen());
            System.out.println(flexibleData.getTopic());
            System.out.println(new String(flexibleData.getData().getBytes()));
        }

    }
}
