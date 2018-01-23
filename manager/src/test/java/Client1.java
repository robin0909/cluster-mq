import com.robin.base.core.ProtrolCore;
import com.robin.base.module.FlexibleData;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

import java.util.List;

public class Client1 {

    public static void main(String[] args) {
//        Buffer buff = Buffer.buffer();
//        buff.appendInt(11).appendString("demo");
//
//        byte[] bytes = buff.getBytes();
//
//        Buffer buffer = Buffer.buffer(bytes);
//        int anInt = buffer.getInt(0);
//        String string = buff.getString(4, buff.length());
//
//        System.out.println(anInt);
//        System.out.println(string);

        Vertx vertx = Vertx.vertx();


        FlexibleData flexibleData = new FlexibleData("demo", FlexibleData.HAND_SHAKE);

        NetClient client = vertx.createNetClient();
        client.connect(9000, "localhost", res->{
            if (res.succeeded()) {
                System.out.println("connect success");

                NetSocket netSocket = res.result();

                netSocket.write(flexibleData.pack());

                netSocket.handler(res1->{

                    List<FlexibleData> flexibleDataList = ProtrolCore.parseFlexibleDatas(res1);
                    for (FlexibleData data : flexibleDataList) {
                        if (data.isHandlShake()) {
                            System.out.println("handshake success");
                        } else if (data.isACK()) {
                            System.out.println("ack");
                        } else if (data.isData()) {
                            Buffer buffer = data.getData();
                            System.out.println(new String(buffer.getBytes()));
                        }
                    }


                });

            } else {
                System.out.println("connect failed");
            }
        });

    }
}
