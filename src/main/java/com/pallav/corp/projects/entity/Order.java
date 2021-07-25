package com.pallav.corp.projects.entity;

import com.pallav.corp.projects.enums.OrderAssignmentStrategy;
import com.pallav.corp.projects.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Order {
    private Integer orderId;

    private Customer customer;
    private Restaurant serviceRestaurant;

    private Map<FoodItem, Integer> foodItems;

    private OrderAssignmentStrategy strategyPreference;

    private OrderStatus orderStatus;
    private Double orderTotal;
}
