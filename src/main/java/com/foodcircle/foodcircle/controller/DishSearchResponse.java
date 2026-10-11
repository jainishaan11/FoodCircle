package com.foodcircle.foodcircle.controller;

public class DishSearchResponse {

    private String userName;
    private String restaurantName;
    private String location;

    private double userAverageRating;
    private long userOrderCount;

    private double overallAverageRating;
    private long totalOrders;
    private long uniqueUsers;

    private String ratingCategory;
    private String popularityTrend;

    public DishSearchResponse(
            String userName,
            String restaurantName,
            String location,
            double userAverageRating,
            long userOrderCount,
            double overallAverageRating,
            long totalOrders,
            long uniqueUsers,
            String ratingCategory,
            String popularityTrend) {

        this.userName = userName;
        this.restaurantName = restaurantName;
        this.location = location;
        this.userAverageRating = userAverageRating;
        this.userOrderCount = userOrderCount;
        this.overallAverageRating = overallAverageRating;
        this.totalOrders = totalOrders;
        this.uniqueUsers = uniqueUsers;
        this.ratingCategory = ratingCategory;
        this.popularityTrend = popularityTrend;
    }

    public String getUserName() {
        return userName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getLocation() {
        return location;
    }

    public double getUserAverageRating() {
        return userAverageRating;
    }

    public long getUserOrderCount() {
        return userOrderCount;
    }

    public double getOverallAverageRating() {
        return overallAverageRating;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public long getUniqueUsers() {
        return uniqueUsers;
    }

    public String getRatingCategory() {
        return ratingCategory;
    }

    public String getPopularityTrend() {
        return popularityTrend;
    }
}