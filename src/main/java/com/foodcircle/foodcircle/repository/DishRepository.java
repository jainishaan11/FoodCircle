package com.foodcircle.foodcircle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodcircle.foodcircle.entity.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {

    Dish findByName(String name);
}