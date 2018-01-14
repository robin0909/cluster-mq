import com.robin.base.module.FlexibleData;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

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
                    FlexibleData flexibleData1 = new FlexibleData(res1);

                    if (flexibleData1.isHandlShake()) {
                        System.out.println("handshake success");
                    } else if (flexibleData1.isACK()) {
                        System.out.println("ack");
                    } else if (flexibleData1.isData()) {
                        Buffer buffer = flexibleData1.getData();
                        System.out.println(new String(buffer.getBytes()));
                    }
                });

            } else {
                System.out.println("connect failed");
            }
        });

    }
}
