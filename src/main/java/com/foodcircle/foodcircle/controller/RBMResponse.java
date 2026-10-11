package com.foodcircle.foodcircle.controller;

public class RBMResponse {

    private String firstEntity;
    private String secondEntity;
    private long orderCount;

    public RBMResponse(String firstEntity, String secondEntity, long orderCount) {
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
        this.orderCount = orderCount;
    }

    public String getFirstEntity() {
        return firstEntity;
    }

    public String getSecondEntity() {
        return secondEntity;
    }

    public long getOrderCount() {
        return orderCount;
    }
}