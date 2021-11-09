package com.raven.rabbitmq.dao;

import com.raven.rabbitmq.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface OrderDAO extends JpaRepository<Order,String>, CrudRepository<Order,String>, JpaSpecificationExecutor<Order> {


}
