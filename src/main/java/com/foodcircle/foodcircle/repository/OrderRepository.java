package com.foodcircle.foodcircle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodcircle.foodcircle.entity.Dish;
import com.foodcircle.foodcircle.entity.Order;
import com.foodcircle.foodcircle.entity.Restaurant;
import com.foodcircle.foodcircle.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByDish(Dish dish);
    List<Order> findByRestaurant(Restaurant restaurant);
    List<Order> findByUser(User user);
    List<Order> findByRestaurant_Name(String name);
    List<Order> findByDish_NameAndRestaurant_Location(String dishName,String location);
    List<Order> findByDish_Name(String name);
    List<Order> findByDish_NameAndRestaurant_Name(String dishName,String restaurantName);
    List<Order> findByDish_NameAndRestaurant_NameAndRestaurant_Location(
    String dishName,String restaurantName,String location);
    List<Order> findByRestaurant_NameAndRestaurant_Location(String name, String location);
    boolean existsByUserAndDishAndRestaurantAndRatingAndOrderedAt(
    User user,
    Dish dish,
    Restaurant restaurant,
    int rating,
    java.time.LocalDateTime orderedAt
    );
}

