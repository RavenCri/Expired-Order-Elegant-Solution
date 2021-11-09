package com.raven.rabbitmq.service;

import com.raven.rabbitmq.config.MQConfig;
import com.raven.rabbitmq.config.OrderConfig;
import com.raven.rabbitmq.dao.OrderDAO;
import com.raven.rabbitmq.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class OrderService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    OrderDAO orderDAO;
    public void addOrder(Order order) {
        order.setPayStatus(OrderConfig.order_no_pay);
        rabbitTemplate.convertAndSend(
                MQConfig.EXCHNAGE_DELAY,
                MQConfig.ROUTINGKEY_QUEUE_ORDER,
                order);
    }

    public void orderPay(String orderId) {
        rabbitTemplate.convertAndSend(
                MQConfig.EXCHNAGE_DELAY,
                MQConfig.ROUTINGKEY_QUEUE_PAY_SUCCESS,
                orderId);
    }
}
