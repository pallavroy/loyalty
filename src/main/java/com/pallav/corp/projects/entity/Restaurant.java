package com.pallav.corp.projects.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Restaurant {
    private String restaurantName;
    private Map<String, RestaurantMenuItem> menu;
    private Double restaurantRating;

    private Integer serviceCapacity;

    private List<Integer> assignedOrderIds;
    private List<Integer> orderHistory;
}
