package com.foodcircle.foodcircle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodcircle.foodcircle.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Restaurant findByNameAndLocation(String name, String location);
}