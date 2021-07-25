package com.pallav.corp.projects.manager;

import com.pallav.corp.projects.entity.FoodItem;
import com.pallav.corp.projects.entity.Order;
import com.pallav.corp.projects.entity.Restaurant;
import com.pallav.corp.projects.enums.OrderAssignmentStrategy;
import com.pallav.corp.projects.exception.AssignmentStrategyNotFoundException;
import com.pallav.corp.projects.exception.NonassignableOrderException;
import com.pallav.corp.projects.repo.RestaurantDB;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantManager {
    private final RestaurantDB restaurantDB = new RestaurantDB();

    public Restaurant assignOrderToRestaurant(Order order) {
        OrderAssignmentStrategy strategyPreference = order.getStrategyPreference();

        switch (strategyPreference) {
            case MIN_PRICE:
                return assignOrderOnLowPriceRestaurant(order);
            case HIGH_RATING:
                return assignOrderToHighRatedRestaurant(order);
            default:
                throw new AssignmentStrategyNotFoundException("Invalid strategy");
        }
    }

    private Restaurant assignOrderToHighRatedRestaurant(Order order) {
        Restaurant restaurant = getHighestRatedOrderServiceableRestaurant(order);
        addOrderToRestaurant(order, restaurant);
        return restaurant;
    }

    private void addOrderToRestaurant(Order order, Restaurant restaurant) {
        restaurant.getAssignedOrderIds().add(order.getOrderId());
        restaurantDB.updateRestaurant(restaurant);
    }

    private Restaurant assignOrderOnLowPriceRestaurant(Order order) {
        return null;
    }

    private Restaurant getHighestRatedOrderServiceableRestaurant(Order order) {
        Set<String> foodItems = order.getFoodItems().keySet().stream()
                .map(FoodItem::getItemName).collect(Collectors.toSet());
        List<Restaurant> ratingSortedServiceableRestaurants = getRatingSortedServiceableRestaurants();
        for (Restaurant restaurant: ratingSortedServiceableRestaurants) {
            Set<String> restaurantMenuItems = restaurant.getMenu().keySet();
            if (restaurantMenuItems.containsAll(foodItems)) return restaurant;
        }

        throw new NonassignableOrderException("No restaurant found on high rating criteria");
    }

    private List<Restaurant> getRatingSortedServiceableRestaurants() {
        List<Restaurant> allRestaurant = restaurantDB.getAllServiceableRestaurants();

        return allRestaurant.stream().sorted((o1, o2) -> o1.getRestaurantRating() > o2.getRestaurantRating() ?
                1 : (!o1.getRestaurantRating().equals(o2.getRestaurantRating()) ? -1 : 0)).collect(Collectors.toList());
    }

    public void updateFulFilledOrderForRestaurant(Restaurant serviceRestaurant, Integer orderId) {
        Restaurant restaurant = restaurantDB.getRestaurant(serviceRestaurant.getRestaurantName());
        List<Integer> newOrderList = restaurant.getAssignedOrderIds().stream().filter(id -> !id.equals(orderId)).collect(Collectors.toList());
        restaurant.setAssignedOrderIds(newOrderList);
        restaurant.getOrderHistory().add(orderId);

        // auto saved as it is call by value which brings object reference
    }

    public void updateCancelledOrderForRestaurant(Restaurant serviceRestaurant, Integer orderId) {
        Restaurant restaurant = restaurantDB.getRestaurant(serviceRestaurant.getRestaurantName());
        List<Integer> newOrderList = restaurant.getAssignedOrderIds().stream().filter(id -> !id.equals(orderId)).collect(Collectors.toList());
        restaurant.setAssignedOrderIds(newOrderList);

        // auto saved as it is call by value which brings object reference
    }
}
