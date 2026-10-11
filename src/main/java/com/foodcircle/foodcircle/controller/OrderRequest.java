package com.foodcircle.foodcircle.controller;

import java.time.LocalDateTime;

public class OrderRequest {

    private String userName;
    private String dishName;
    private String restaurantName;
    private String userEmail;
    private LocalDateTime orderedAt;
    private String restaurantLocation;
    private int rating;

    public OrderRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getUserEmail() {
    return userEmail;
    }

    public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
    }
    
    public String getRestaurantLocation() {
    return restaurantLocation;
    }

    public void setRestaurantLocation(String restaurantLocation) {
    this.restaurantLocation = restaurantLocation;
}
}