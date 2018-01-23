import com.robin.base.core.ProtrolCore;
import com.robin.base.module.FlexibleData;
import com.robin.base.type.SubScribeType;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.List;


public class Client2 {

    public static void main(String[] args) throws InterruptedException {

        Vertx vertx = Vertx.vertx();

        NetSocket[] netSocket = new NetSocket[1];

        FlexibleData flexibleData = new FlexibleData("demo", FlexibleData.HAND_SHAKE);

        NetClient client = vertx.createNetClient();
        client.connect(9000, "localhost", res->{
            if (res.succeeded()) {
                System.out.println("connect success");

                netSocket[0] = res.result();

//                netSocket[0].setWriteQueueMaxSize(1024);

                netSocket[0].write(flexibleData.pack());

                netSocket[0].handler(res1->{
//                    List<FlexibleData> flexibleDataList = ProtrolCore.parseFlexibleDatas(res1);
//                    for (FlexibleData data : flexibleDataList) {
//                        String topic = data.getTopic();
//                        System.out.println(String.format("ack success , topic: %s", topic));
//                    }

                });

            } else {
                System.out.println("connect failed");
            }
        });


        Thread.sleep(3000L);

        for (int i = 0; i < 10000; i++) {

            JsonObject json = new JsonObject();
            json.put("index", i);
            json.put("name", "robin");

            Buffer buffer = Buffer.buffer();
            buffer.appendString(json.toString());

            FlexibleData data = new FlexibleData("demo", SubScribeType.ONE_TO_ONE, buffer);

            netSocket[0].write(data.pack());

        }
    }
}
