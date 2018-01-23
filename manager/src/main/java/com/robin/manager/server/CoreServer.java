package com.robin.manager.server;

import com.robin.base.core.ProtrolCore;
import com.robin.base.module.FlexibleData;
import com.robin.base.type.SubScribeType;
import com.robin.manager.core.BrokerNodeManager;
import com.robin.manager.core.QueueCore;
import com.robin.manager.model.TopicWrap;
import com.robin.manager.service.RemoteTopicService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CoreServer extends AbstractVerticle {

    final private static Logger logger = LoggerFactory.getLogger(CoreServer.class);

    @Autowired
    private NetServerOptions netServerOptions;

    @Autowired
    private Vertx vertx;

    @Autowired
    private QueueCore queueCore;

    @Autowired
    private BrokerNodeManager brokerNodeManager;

    @Autowired
    private RemoteTopicService remoteTopicService;

    // connectId  -  NetSocket
    final static private Map<String, NetSocket> connectsMap = new ConcurrentHashMap<>();

    // topic  -  connectId
    final static private Map<String, List<String>> topicListMap = new ConcurrentHashMap<>();

    // connectId  -  topic
    final private Map<String, String> connectTopicMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void acceptData() {
        NetServer server = vertx.createNetServer(netServerOptions);
        server.connectHandler(socket->{
            String handlerID = socket.writeHandlerID();
            socket.handler(buffer->{
//                FlexibleData flexibleData = new FlexibleData(buffer);

                List<FlexibleData> flexibleDataList = ProtrolCore.parseFlexibleDatas(buffer);
                for (FlexibleData flexibleData : flexibleDataList) {
                    if (flexibleData.isData()) {
                        // 数据包
                        TopicWrap topicWrap = brokerNodeManager.getTopicWrapMap().get(flexibleData.getTopic());
                        if (topicWrap == null) {
                            // 没有添加topic 就发送数据情况
                            topicWrap = new TopicWrap(flexibleData.getTopic(), SubScribeType.ONE_TO_ONE.getType());
                            brokerNodeManager.addTopic(topicWrap);
//                        remoteTopicService.addTopic(topicWrap);
                            flexibleData.setType(SubScribeType.ONE_TO_ONE);
                        } else {
                            flexibleData.setType(SubScribeType.parse(topicWrap.getType()));
                        }

                        queueCore.addData(flexibleData);
                    } else if (flexibleData.isHandlShake()) {
                        // 握手包
                        String topic = flexibleData.getTopic();
                        List<String> list = topicListMap.get(topic);

                        if (list != null) {
                            list.add(handlerID);
                        } else {
                            list = new ArrayList<>();
                            list.add(handlerID);
                            topicListMap.put(topic, list);
                        }

                        connectTopicMap.put(handlerID, topic);
                    }

                    // ack
                    flexibleData = new FlexibleData(flexibleData.getTopic(), FlexibleData.ACK);
                    socket.write(flexibleData.pack());
                }
            });

            connectsMap.put(handlerID, socket);
        });

        server.listen(res->{
            if (res.succeeded()) {
                NetServer netServer = res.result();
                int port = netServer.actualPort();
                logger.info("--------------------------------");
                logger.info(String.format("tcp server lister port: %d", port));
            }
        });
    }

    public static void send(FlexibleData flexibleData) {

        String topic = flexibleData.getTopic();
        ArrayList<String> connectList = new ArrayList<>();
        connectList.addAll(topicListMap.get(topic));

        for (String connetId : connectList) {
            NetSocket netSocket = connectsMap.get(connetId);
            if (netSocket != null) {
                netSocket.write(flexibleData.pack());
//                netSocket.handler(buf->{
//                    FlexibleData data = new FlexibleData(buf);
//                    if (data.isACK()) {
//                        System.out.println("转发成功");
//                    }
//
            }
        }
    }

}
