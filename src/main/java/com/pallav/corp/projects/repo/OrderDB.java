package com.pallav.corp.projects.repo;

import com.pallav.corp.projects.entity.Order;
import com.pallav.corp.projects.util.OrderingConstants;

import java.util.HashMap;
import java.util.Map;

public class OrderDB {
    private static Map<Integer, Order> orders = new HashMap<>();

    private static Integer orderId = OrderingConstants.ORDER_COUNTER;

    public void addOrder(Order order) {
        Integer orderId = createNewOrderId();
        order.setOrderId(orderId);
        orders.put(orderId, order);
    }

    public void updateOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }

    public Order getOrderById(Integer id) throws Exception {
        if (orders.containsKey(id)) {
            return orders.get(id);
        } else {
            throw new Exception("No such order");
        }
    }

    public Map<Integer, Order> getAllOrders() {
        return orders;
    }

    public Integer getLastCreatedOrderId() {
        return orderId;
    }

    private Integer createNewOrderId() {
        return ++orderId;
    }
}
