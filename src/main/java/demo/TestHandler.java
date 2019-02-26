package demo;

import com.zx.rabbit.consume.Consumer;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

/**
 * @author xzhen
 * @created 14:20 28/01/2019
 * @description TODO
 */
//@RabbitMQHandler("sms")
public class TestHandler implements Consumer<SecurityProperties.User> {

    @Override
    public boolean handle(SecurityProperties.User object) {
        boolean success = true;
        if (success) {
            return true;
        }
        return false;// retry
    }

    @Override
    public void handleRetryMax(SecurityProperties.User object) {
        // retry count max
    }
}
