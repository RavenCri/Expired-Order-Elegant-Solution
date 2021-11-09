package com.raven.rabbitmq.demo;

import com.raven.rabbitmq.config.MQConfig;
import com.raven.rabbitmq.config.OrderConfig;
import com.raven.rabbitmq.dao.OrderDAO;
import com.raven.rabbitmq.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class Consumer {
    @Autowired
    OrderDAO orderDAO;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MQConfig.QUEUE_ORDER)
    public void handlerOrder(@Payload Order order, Message message){

        order.setPayStatus(OrderConfig.order_no_pay);
        order.setCreateTime(LocalDateTime.now());
        // 保存订单
        orderDAO.save(order);
        System.out.println("新建了一个订单， orderId:"+order.getId());

        System.out.println("审核链接：http://localhost:8081/paySuccess?orderId="+order.getId());
        // 发送该订单至核验队列
        rabbitTemplate.convertAndSend(
                MQConfig.EXCHNAGE_DELAY,
                MQConfig.ROUTINGKEY_QUEUE_CHECK_ORDER,
                order);
    }
    // 核验队列（延迟）后 会将消息发送至死信队列。死信队列判断该订单是否过期
    @RabbitListener(queues = MQConfig.QUEUE_DELAY)
    public void handlerDelayOrder(@Payload Order order, Message message){
        System.out.println(order.toString());

        // 查找数据库该订单是否已支付
        Optional<Order> od = orderDAO.findById(order.getId());
        od.ifPresent(e->{
            if(e.getPayStatus() == OrderConfig.order_pay){
                System.out.println(String.format("订单id:%s支付成功~",e.getId()));
            }else{
                e.setPayStatus(OrderConfig.order_expired);
                orderDAO.save(e);
                System.out.println(String.format("订单id:%s长时间未支付，已过期",e.getId()));
            }
        });
    }

    // 支付成功
    @RabbitListener(queues = MQConfig.QUEUE_PAY_SUCCESS)
    public void handlerPayOrder(@Payload String orderId, Message message){
        if(orderId == null || orderId.equals("")){
            return ;
        }
        Optional<Order> orderOptional = orderDAO.findById(orderId);
        orderOptional.ifPresent(order->{
            order.setPayStatus(OrderConfig.order_pay);
            order.setPayTime(LocalDateTime.now());
            orderDAO.save(order);
        });

    }
}

