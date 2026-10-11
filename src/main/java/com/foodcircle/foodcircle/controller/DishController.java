package com.foodcircle.foodcircle.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodcircle.foodcircle.entity.Dish;
import com.foodcircle.foodcircle.repository.DishRepository;

@RestController
public class DishController {

    private final DishRepository dishRepository;

    public DishController(DishRepository dishRepository) {
    this.dishRepository = dishRepository;
    }

    @GetMapping("/dishes")
    public List<Dish> getDishes() {
        return dishRepository.findAll();
    }

    @PostMapping("/dishes")
    public Dish addDish(@RequestBody DishRequest request) {

    Dish dish = new Dish();
    dish.setName(request.getName());

    return dishRepository.save(dish);
    }
}