package com.pallav.corp.projects.repo;

import com.pallav.corp.projects.entity.Restaurant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantDB {
    private static final Map<String, Restaurant> restaurants = new HashMap<>();

    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getRestaurantName(), restaurant);
    }

    public List<Restaurant> getAllServiceableRestaurants() {
        return restaurants.values().stream()
                .filter(restaurant ->
                        restaurant.getServiceCapacity() <= restaurant.getAssignedOrderIds().size()).collect(Collectors.toList());
    }

    public Restaurant getRestaurant(String restaurantName) {
        if (restaurants.containsKey(restaurantName)) {
            return restaurants.get(restaurantName);
        } else {
            System.out.println("No such restaurant");
        }
        return null;
    }

    public void removeRestaurant(String restaurantName) {
        restaurants.remove(restaurantName);
    }

    public void updateRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getRestaurantName(), restaurant);
    }
}
