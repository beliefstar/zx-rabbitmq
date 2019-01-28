//package com.sunflower.rabbit;
//
//import com.sunflower.rabbit.common.model.Message;
//import com.sunflower.rabbit.produce.Producer;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class RabbitApplicationTests {
//
//    @Autowired
//    private Producer producer;
//
//    @Test
//    public void contextLoads() {
//
//        Message message = new Message();
//        message.setExchange("topic_test3");
//        message.setRouteKey("info");
//        message.setData("hello World");
//
//        while (true) {
//            producer.send(message);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
//
