package com.raven.rabbitmq;

import com.raven.rabbitmq.config.MQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitmqApplication.class)
public class RabbitmqApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void sendDelayMessage() throws InterruptedException {
        System.out.println("创建了一笔订单：开始时间：" + System.currentTimeMillis());
        rabbitTemplate.convertAndSend(
                MQConfig.EXCHNAGE_DELAY,
                MQConfig.ROUTINGKEY_QUEUE_ORDER,
                "创建了一笔订单"
        );

        Thread.sleep(20000);
    }
}