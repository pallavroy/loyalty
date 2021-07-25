package com.pallav.corp.projects.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantMenuItem {
    private RestaurantFoodItem foodItem;
    private Double itemPrice;
}
