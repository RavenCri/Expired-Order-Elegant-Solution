package com.raven.rabbitmq.controller;


import com.raven.rabbitmq.config.MQConfig;
import com.raven.rabbitmq.model.Order;
import com.raven.rabbitmq.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class PayController {

    @Autowired
    OrderService orderService;
    @PostMapping("/createOrder")
    public String createOrder(@RequestBody Order order){

        orderService.addOrder(order);

        return "已生成订单，请在10s内完成支付";
    }

    @GetMapping("/paySuccess")
    public String paySuccess(String orderId){
        orderService.orderPay(orderId);

        return "您已支付！祝您生活愉快~";
    }
}
