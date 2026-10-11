package com.foodcircle.foodcircle.controller;

public class DishRatingResponse {

    private String dishName;
    private String restaurantName;
    private String location;
    private double averageRating;
    private long totalOrders;
    private long uniqueUsers;
    private String category;

    public DishRatingResponse(
            String dishName,
            String restaurantName,
            String location,
            double averageRating,
            long totalOrders,
            long uniqueUsers,
            String category) {

        this.dishName = dishName;
        this.restaurantName = restaurantName;
        this.location = location;
        this.averageRating = averageRating;
        this.totalOrders = totalOrders;
        this.uniqueUsers = uniqueUsers;
        this.category = category;
    }

    public String getDishName() {
        return dishName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getLocation() {
        return location;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public long getUniqueUsers() {
        return uniqueUsers;
    }

    public String getCategory() {
        return category;
    }
}