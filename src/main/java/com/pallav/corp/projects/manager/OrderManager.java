package com.pallav.corp.projects.manager;

import com.pallav.corp.projects.entity.FoodItem;
import com.pallav.corp.projects.entity.Order;
import com.pallav.corp.projects.entity.Restaurant;
import com.pallav.corp.projects.entity.RestaurantMenuItem;
import com.pallav.corp.projects.enums.OrderStatus;
import com.pallav.corp.projects.exception.AssignmentStrategyNotFoundException;
import com.pallav.corp.projects.exception.NonassignableOrderException;
import com.pallav.corp.projects.repo.OrderDB;

import java.util.Map;

public class OrderManager {
    private static final OrderDB orderDB = new OrderDB();

    private final RestaurantManager restaurantManager = new RestaurantManager();

    public void createAndAssignOrder(Order order) {
        try {
            Restaurant restaurant = restaurantManager.assignOrderToRestaurant(order);
            updateOrderOnRestaurantAssignment(order, restaurant);
        } catch (AssignmentStrategyNotFoundException e) {
            order.setOrderStatus(OrderStatus.REJECTED);
        } catch (NonassignableOrderException e) {
            order.setOrderStatus(OrderStatus.PENDING);
        }

        orderDB.addOrder(order);
    }

    public void updateOrderStatus(Integer orderId, OrderStatus orderStatus) throws Exception {
        switch (orderStatus) {
            case IN_PROGRESS:
                handleOrderStatusChangeToInProgress(orderId);
                break;
            case CANCELLED:
                handleOrderStatusChangeToCancelled(orderId);
                break;
            case FULFILLED:
                handleOrderStatusChangeToFulfilled(orderId);
                break;
            default:
                throw new Exception("Invalid change operation");
        }
    }

    private void handleOrderStatusChangeToFulfilled(Integer orderId) throws Exception {
        Order order = orderDB.getOrderById(orderId);
        order.setOrderStatus(OrderStatus.FULFILLED);

        Restaurant serviceRestaurant = order.getServiceRestaurant();
        // update order in rest -> remove from assigned list and add in history
        restaurantManager.updateFulFilledOrderForRestaurant(serviceRestaurant, orderId);
    }

    private void handleOrderStatusChangeToCancelled(Integer orderId) throws Exception {
        Order order = orderDB.getOrderById(orderId);
        order.setOrderStatus(OrderStatus.CANCELLED);

        Restaurant serviceRestaurant = order.getServiceRestaurant();
        // update order in rest -> remove from assigned
        restaurantManager.updateCancelledOrderForRestaurant(serviceRestaurant, orderId);
    }

    private void handleOrderStatusChangeToInProgress(Integer orderId) throws Exception {
        Order order = orderDB.getOrderById(orderId);
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderDB.updateOrder(order);
    }

    private void updateOrderOnRestaurantAssignment(Order order, Restaurant restaurant) {
        order.setOrderStatus(OrderStatus.ASSIGNED);
        order.setServiceRestaurant(restaurant);

        // set order total price
        double orderTotal = 0.0;
        Map<String, RestaurantMenuItem> restaurantMenu = restaurant.getMenu();
        for (Map.Entry<FoodItem, Integer> orderItem: order.getFoodItems().entrySet()) {
            FoodItem foodItem = orderItem.getKey();
            Integer itemQty = orderItem.getValue();

            RestaurantMenuItem restaurantMenuItem = restaurantMenu.get(foodItem.getItemName());
            Double restaurantMenuItemItemPrice = restaurantMenuItem.getItemPrice();
            orderTotal += restaurantMenuItemItemPrice * itemQty;
        }

        order.setOrderTotal(orderTotal);
    }
}
