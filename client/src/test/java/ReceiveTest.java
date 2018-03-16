import com.robin.client.ReceiveListener;
import com.robin.client.Receiver;
import io.vertx.core.json.JsonObject;

public class ReceiveTest {

    public static void main(String[] args) throws InterruptedException {
        Receiver receiver = Receiver.init("127.0.0.1:9000");

        Thread.sleep(1000L);

        receiver.addListener(new ReceiveListenerTest());
    }

    public static class ReceiveListenerTest implements ReceiveListener {

        @Override
        public String topic() {
            return "demo";
        }

        @Override
        public void onReceive(JsonObject data) {
            System.out.println(data.toString());

            Long time = data.getLong("time");
            long betweenTime = System.currentTimeMillis() - time;

            System.out.println("current: "+System.currentTimeMillis()+", between: "+betweenTime);
        }
    }
}
