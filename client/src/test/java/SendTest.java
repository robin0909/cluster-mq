import com.robin.base.module.FlexibleData;
import com.robin.base.type.SubScribeType;
import com.robin.client.Sender;
import io.vertx.core.json.JsonObject;

public class SendTest {

    public static void main(String[] args) {

        Sender sender = Sender.init("127.0.0.1:9000", "demo");

        for (int i = 0; i < 100; i++) {
            JsonObject data = new JsonObject();
            data.put("index", i);
            data.put("name", "robin"+i);

            sender.send(data, SubScribeType.ONE_TO_MANY);
        }
    }


}
