package demo;

import com.sunflower.rabbit.common.model.Message;
import com.sunflower.rabbit.produce.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xzhen
 * @created 14:22 28/01/2019
 * @description TODO
 */
@Component
public class TestProducer {

    @Autowired
    private Producer producer;

    public void doSend() {
        Object obj = new Object();

        Message message = new Message();
        message.setExchange("exchange");
        message.setRouteKey("routeKey");
        message.setRetryCount(5);
        message.setData(obj);
        producer.send(message); // send message
    }
}
