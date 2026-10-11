package com.foodcircle.foodcircle.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodcircle.foodcircle.entity.Dish;
import com.foodcircle.foodcircle.entity.Order;
import com.foodcircle.foodcircle.entity.Restaurant;
import com.foodcircle.foodcircle.entity.User;
import com.foodcircle.foodcircle.repository.DishRepository;
import com.foodcircle.foodcircle.repository.OrderRepository;
import com.foodcircle.foodcircle.repository.RestaurantRepository;
import com.foodcircle.foodcircle.repository.UserRepository;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderController(OrderRepository orderRepository,
                       DishRepository dishRepository,
                       UserRepository userRepository,
                       RestaurantRepository restaurantRepository) {

    this.orderRepository = orderRepository;
    this.dishRepository = dishRepository;
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/orders")
    public ResponseEntity<?> addOrder(@RequestBody OrderRequest request) {

    if (request.getUserName() == null || request.getUserName().isBlank()
            || request.getUserEmail() == null || request.getUserEmail().isBlank()
            || request.getDishName() == null || request.getDishName().isBlank()
            || request.getRestaurantName() == null || request.getRestaurantName().isBlank()
            || request.getRestaurantLocation() == null || request.getRestaurantLocation().isBlank()
            || request.getOrderedAt() == null
            || request.getRating() < 1 || request.getRating() > 5) {

        return ResponseEntity.badRequest().body("Please fill in all fields correctly.");
    }

    User user = userRepository.findByNameAndEmail(
            request.getUserName(),
            request.getUserEmail()
    );

    if (user == null) {
        return ResponseEntity.badRequest().body("User not found. Please log in again.");
    }

    Restaurant restaurant = restaurantRepository.findByNameAndLocation(
            request.getRestaurantName(),
            request.getRestaurantLocation()
    );

    if (restaurant == null) {
        restaurant = new Restaurant();
        restaurant.setName(request.getRestaurantName());
        restaurant.setLocation(request.getRestaurantLocation());
        restaurant = restaurantRepository.save(restaurant);
    }

    Dish dish = dishRepository.findByName(request.getDishName());

    if (dish == null) {
        dish = new Dish();
        dish.setName(request.getDishName());
        dish = dishRepository.save(dish);
    }

    boolean duplicate = orderRepository.existsByUserAndDishAndRestaurantAndRatingAndOrderedAt(
            user,
            dish,
            restaurant,
            request.getRating(),
            request.getOrderedAt()
    );

    if (duplicate) {
        return ResponseEntity.status(409).body("Sorry, the order already exists!");
    }

    Order order = new Order();
    order.setUser(user);
    order.setDish(dish);
    order.setRestaurant(restaurant);
    order.setOrderedAt(request.getOrderedAt());
    order.setRating(request.getRating());

    Order savedOrder = orderRepository.save(order);

    return ResponseEntity.status(201).body(savedOrder);
}
   

    @GetMapping("/rbm/dish/{dishName}")
    public List<RBMResponse> rbmByDish(@org.springframework.web.bind.annotation.PathVariable String dishName) {

    Dish dish = dishRepository.findByName(dishName);

    List<Order> orders = orderRepository.findByDish(dish);

    Map<String, Integer> countMap = new HashMap<>();

    for (Order order : orders) {

        String key = order.getUser().getName() + "|" +
                     order.getRestaurant().getName();

        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
    }

    List<RBMResponse> result = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {

        String[] parts = entry.getKey().split("\\|");

        result.add(
            new RBMResponse(
                parts[0],
                parts[1],
                entry.getValue()
            )
        );
    }

    return result;
    }

    @GetMapping("/rbm/restaurant/{restaurantName}/{restaurantLocation}")
    public List<RBMResponse> rbmByRestaurant(
    @org.springframework.web.bind.annotation.PathVariable String restaurantName,
    @org.springframework.web.bind.annotation.PathVariable String restaurantLocation) {
    
    Restaurant restaurant = restaurantRepository.findByNameAndLocation(
    restaurantName,
    restaurantLocation
    );

    if (restaurant == null) {
    return new ArrayList<>();
    }

    List<Order> orders = orderRepository.findByRestaurant(restaurant);

    Map<String, Integer> countMap = new HashMap<>();

    for (Order order : orders) {

        String key = order.getUser().getName() + "|" +
                     order.getDish().getName();

        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
    }

    List<RBMResponse> result = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {

        String[] parts = entry.getKey().split("\\|");

        result.add(
            new RBMResponse(
                parts[0],
                parts[1],
                entry.getValue()
            )
        );
    }

    return result;

    }
    
        @GetMapping("/rbm/user/{userName}/{userEmail}")
public List<UserSearchResponse> rbmByUser(
        @PathVariable String userName,
        @PathVariable String userEmail) {

    // Find the user
    User user = userRepository.findByNameAndEmail(userName, userEmail);

    if (user == null) {
        return new ArrayList<>();
    }

    // Get all orders placed by this user
    List<Order> orders = orderRepository.findByUser(user);

    // Group orders by dish + restaurant + location
    Map<String, List<Order>> groupedOrders = new HashMap<>();

    for (Order order : orders) {
        String key = order.getDish().getName() + "|"
                + order.getRestaurant().getName() + "|"
                + order.getRestaurant().getLocation();

        groupedOrders.computeIfAbsent(key, k -> new ArrayList<>())
                .add(order);
    }

    List<UserSearchResponse> result = new ArrayList<>();

    // Process each dish at each restaurant location
    for (List<Order> userOrders : groupedOrders.values()) {

        Order firstOrder = userOrders.get(0);

        String dishName = firstOrder.getDish().getName();
        String restaurantName = firstOrder.getRestaurant().getName();
        String location = firstOrder.getRestaurant().getLocation();

        // Calculate this user's average rating
        double ratingSum = 0;

        for (Order order : userOrders) {
            ratingSum += order.getRating();
        }

        double userAverageRating = Math.round(
                (ratingSum / userOrders.size()) * 100.0
        ) / 100.0;

        long userOrderCount = userOrders.size();

        // Reuse the existing Rating Engine
        DishRatingResponse ratingResponse = getDishRating(
                dishName,
                restaurantName,
                location
        );

        double overallAverageRating = ratingResponse.getAverageRating();
        long totalOrders = ratingResponse.getTotalOrders();
        long uniqueUsers = ratingResponse.getUniqueUsers();
        String ratingCategory = ratingResponse.getCategory();

        // Reuse the existing Popularity Engine
        Map<String, Object> popularityResponse = getOutletPopularity(
                restaurantName,
                location
        );

        String popularityTrend =
                (String) popularityResponse.get("trend");

        // Build the response for the frontend
        result.add(new UserSearchResponse(
                dishName,
                restaurantName,
                location,
                userAverageRating,
                userOrderCount,
                overallAverageRating,
                totalOrders,
                uniqueUsers,
                ratingCategory,
                popularityTrend
        ));
    }

    return result;
    }

        @GetMapping("/rbm/search/restaurant")
public List<RestaurantSearchResponse> searchByRestaurant(
        @org.springframework.web.bind.annotation.RequestParam String restaurantName,
        @org.springframework.web.bind.annotation.RequestParam(required = false) String location) {

    List<Order> orders;

    if (location == null || location.isBlank()) {
        // All locations for this restaurant
        orders = orderRepository.findByRestaurant_Name(restaurantName);
    } else {
        // Only this restaurant at the specified location
        orders = orderRepository.findByRestaurant_NameAndRestaurant_Location(
                restaurantName, location);
    }

    Map<String, List<Order>> groupedOrders = new HashMap<>();

    // Group by user, dish and location
    for (Order order : orders) {
        String key = order.getUser().getId() + "|"
                + order.getDish().getName() + "|"
                + order.getRestaurant().getLocation();

        groupedOrders.computeIfAbsent(key, k -> new ArrayList<>())
                .add(order);
    }

    List<RestaurantSearchResponse> result = new ArrayList<>();

    for (List<Order> userOrders : groupedOrders.values()) {

        Order firstOrder = userOrders.get(0);

        String dishName = firstOrder.getDish().getName();
        String userName = firstOrder.getUser().getName();
        String actualLocation = firstOrder.getRestaurant().getLocation();

        // This user's average rating
        double ratingSum = 0;

        for (Order order : userOrders) {
            ratingSum += order.getRating();
        }

        double userAverageRating = Math.round(
                (ratingSum / userOrders.size()) * 100.0) / 100.0;

        long userOrderCount = userOrders.size();

        // Existing Rating Engine
        DishRatingResponse ratingResponse = getDishRating(
                dishName, restaurantName, actualLocation);

        // Existing Popularity Engine
        Map<String, Object> popularityResponse = getOutletPopularity(
                restaurantName, actualLocation);

        String popularityTrend =
                (String) popularityResponse.get("trend");

        result.add(new RestaurantSearchResponse(
                dishName,
                userName,
                actualLocation,
                userAverageRating,
                userOrderCount,
                ratingResponse.getAverageRating(),
                ratingResponse.getTotalOrders(),
                ratingResponse.getUniqueUsers(),
                ratingResponse.getCategory(),
                popularityTrend
        ));
    }

    return result;
}

        @GetMapping("/rbm/search/dish")
public List<DishSearchResponse> searchByDish(
        @org.springframework.web.bind.annotation.RequestParam String dishName,
        @org.springframework.web.bind.annotation.RequestParam(required = false) String restaurantName,
        @org.springframework.web.bind.annotation.RequestParam(required = false) String location) {

    List<Order> orders;

    if (restaurantName == null || restaurantName.isBlank()) {
        if (location == null || location.isBlank()) {
            // Dish at all restaurants and all locations
            orders = orderRepository.findByDish_Name(dishName);
        } else {
            // Dish at all restaurants in one location
            orders = orderRepository.findByDish_NameAndRestaurant_Location(
                    dishName, location);
        }
    } else {
        if (location == null || location.isBlank()) {
            // Dish at one restaurant across all locations
            orders = orderRepository.findByDish_NameAndRestaurant_Name(
                    dishName, restaurantName);
        } else {
            // Dish at one restaurant and one location
            orders = orderRepository
                    .findByDish_NameAndRestaurant_NameAndRestaurant_Location(
                            dishName, restaurantName, location);
        }
    }

    Map<String, List<Order>> groupedOrders = new HashMap<>();

    // Group by user, restaurant and location
    for (Order order : orders) {
        String key = order.getUser().getId() + "|"
                + order.getRestaurant().getName() + "|"
                + order.getRestaurant().getLocation();

        groupedOrders.computeIfAbsent(key, k -> new ArrayList<>())
                .add(order);
    }

    List<DishSearchResponse> result = new ArrayList<>();

    for (List<Order> userOrders : groupedOrders.values()) {

        Order firstOrder = userOrders.get(0);

        String userName = firstOrder.getUser().getName();
        String restaurant = firstOrder.getRestaurant().getName();
        String actualLocation = firstOrder.getRestaurant().getLocation();

        // Calculate this user's average rating
        double ratingSum = 0;

        for (Order order : userOrders) {
            ratingSum += order.getRating();
        }

        double userAverageRating = Math.round(
                (ratingSum / userOrders.size()) * 100.0) / 100.0;

        long userOrderCount = userOrders.size();

        // Existing Rating Engine
        DishRatingResponse ratingResponse = getDishRating(
                dishName, restaurant, actualLocation);

        // Existing Popularity Engine
        Map<String, Object> popularityResponse = getOutletPopularity(
                restaurant, actualLocation);

        String popularityTrend =
                (String) popularityResponse.get("trend");

        result.add(new DishSearchResponse(
                userName,
                restaurant,
                actualLocation,
                userAverageRating,
                userOrderCount,
                ratingResponse.getAverageRating(),
                ratingResponse.getTotalOrders(),
                ratingResponse.getUniqueUsers(),
                ratingResponse.getCategory(),
                popularityTrend
        ));
    }

    return result;
}


    @GetMapping("/rbm/restaurant/all/{restaurantName}")
    public List<RBMResponse> rbmByRestaurantAllLocations(
        @org.springframework.web.bind.annotation.PathVariable String restaurantName) {

    List<Order> orders = orderRepository.findByRestaurant_Name(restaurantName);

    Map<String, Integer> countMap = new HashMap<>();

    for (Order order : orders) {

        String key = order.getUser().getName() + "|" +
             order.getDish().getName() + "|" +
             order.getRestaurant().getLocation();

        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
    }

    List<RBMResponse> result = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {

        String[] parts = entry.getKey().split("\\|");

        result.add(
        new RBMResponse(
        parts[0],
        parts[1] + " @ " + parts[2],
        entry.getValue()
        )
        );
    }

    return result;
    }
    @GetMapping("/rbm/dish/{dishName}/location/{location}")
    public List<RBMResponse> rbmByDishAndLocation(
        @PathVariable String dishName,
        @PathVariable String location) {

    List<Order> orders =
            orderRepository.findByDish_NameAndRestaurant_Location(
                    dishName, location);

    Map<String, Integer> countMap = new HashMap<>();

    for (Order order : orders) {
        String key = order.getUser().getName() + "|" +
                     order.getRestaurant().getName();

        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
    }

    List<RBMResponse> result = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
        String[] parts = entry.getKey().split("\\|");

        result.add(new RBMResponse(
                parts[0],
                parts[1],
                entry.getValue()
        ));
    }

    return result;
    }
    @GetMapping("/rbm/dish/all/{dishName}")
    public List<RBMResponse> rbmByDishAllRestaurants(
        @PathVariable String dishName) {

    List<Order> orders =
            orderRepository.findByDish_Name(dishName);

    Map<String, Integer> countMap = new HashMap<>();

    for (Order order : orders) {
        String key = order.getUser().getName() + "|" +
                     order.getRestaurant().getName() + "|" +
                     order.getRestaurant().getLocation();

        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
    }

    List<RBMResponse> result = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
        String[] parts = entry.getKey().split("\\|");

        result.add(new RBMResponse(
                parts[0],
                parts[1] + " @ " + parts[2],
                entry.getValue()
        ));
    }

    return result;
    }
    @GetMapping("/rbm/dish/{dishName}/restaurant/{restaurantName}")
    public List<RBMResponse> rbmByDishAndRestaurant(
        @PathVariable String dishName,
        @PathVariable String restaurantName) {

    List<Order> orders =
            orderRepository.findByDish_NameAndRestaurant_Name(
                    dishName, restaurantName);

    Map<String, Integer> countMap = new HashMap<>();

    for (Order order : orders) {
        String key = order.getUser().getName() + "|" +
                     order.getRestaurant().getLocation();

        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
    }

    List<RBMResponse> result = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
        String[] parts = entry.getKey().split("\\|");

        result.add(new RBMResponse(
                parts[0],
                restaurantName + " @ " + parts[1],
                entry.getValue()
        ));
    }

    return result;
    }
    @GetMapping("/rbm/dish/{dishName}/restaurant/{restaurantName}/location/{location}")
    public List<RBMResponse> rbmByDishRestaurantAndLocation(
        @PathVariable String dishName,
        @PathVariable String restaurantName,
        @PathVariable String location) {

    List<Order> orders =
            orderRepository.findByDish_NameAndRestaurant_NameAndRestaurant_Location(
                    dishName, restaurantName, location);

    Map<String, Integer> countMap = new HashMap<>();

    for (Order order : orders) {
        String userName = order.getUser().getName();

        countMap.put(
                userName,
                countMap.getOrDefault(userName, 0) + 1
        );
    }

    List<RBMResponse> result = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
        result.add(new RBMResponse(
                entry.getKey(),
                restaurantName + " @ " + location,
                entry.getValue()
        ));
    }

    return result;
    }
        @GetMapping("/rating/dish/{dishName}/restaurant/{restaurantName}/location/{location}")
        public DishRatingResponse getDishRating(
            @PathVariable String dishName,
            @PathVariable String restaurantName,
            @PathVariable String location) {

        List<Order> orders =
                orderRepository.findByDish_NameAndRestaurant_NameAndRestaurant_Location(
                        dishName, restaurantName, location);

        if (orders.isEmpty()) {
            return new DishRatingResponse(
                    dishName, restaurantName, location,
                    0.0, 0, 0, "Insufficient Evidence");
        }

        Map<Long, List<Integer>> userRatings = new HashMap<>();

        for (Order order : orders) {
            Long userId = order.getUser().getId();

            userRatings.putIfAbsent(userId, new ArrayList<>());
            userRatings.get(userId).add(order.getRating());
        }

        double sumOfUserAverages = 0;

        for (List<Integer> ratings : userRatings.values()) {
            double userTotal = 0;

            for (int rating : ratings) {
                userTotal += rating;
            }

            sumOfUserAverages += userTotal / ratings.size();
        }

        double averageRating =
                sumOfUserAverages / userRatings.size();

        long totalOrders = orders.size();
        long uniqueUsers = userRatings.size();

        String category;

        if (averageRating >= 4 && totalOrders >= 20) {
            category = "Bestseller";
        } else if (averageRating <= 3 && totalOrders >= 20) {
            category = "Check It Out / Find the Reason";
        } else if (averageRating >= 3 && totalOrders >= 10) {
            category = "Worth Trying";
        } else if (averageRating < 3 && totalOrders < 10) {
            category = "Not Worth It";
        } else if (averageRating > 4 && totalOrders < 20) {
            category = "Promising, Not Enough Orders";
        } else {
            category = "Insufficient Evidence / Needs Review";
        }

        return new DishRatingResponse(
                dishName, restaurantName, location,
                Math.round(averageRating * 100.0) / 100.0,
                totalOrders, uniqueUsers, category);
    }
    @GetMapping("/popularity/restaurant/{restaurantName}/location/{location}")
    public Map<String, Object> getOutletPopularity(
            @PathVariable String restaurantName,
            @PathVariable String location) {

        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        LocalDateTime currentStart = today.minusDays(7);
        LocalDateTime previousStart = today.minusDays(14);

        List<Order> allOrders =
                orderRepository.findByRestaurant_NameAndRestaurant_Location(
                        restaurantName, location);

        long currentOrders = 0;
        long previousOrders = 0;

        Set<Long> currentUsers = new HashSet<>();
        Set<Long> previousUsers = new HashSet<>();

        for (Order order : allOrders) {
            LocalDateTime time = order.getOrderedAt();
            Long userId = order.getUser().getId();

            if (time == null || userId == null) {
                continue;
            }

            if (!time.isBefore(currentStart) && time.isBefore(today)) {
                currentOrders++;
                currentUsers.add(userId);
            } else if (!time.isBefore(previousStart)
                    && time.isBefore(currentStart)) {
                previousOrders++;
                previousUsers.add(userId);
            }
        }

        String trend;

        if (currentOrders == 0 && previousOrders == 0) {
            trend = "Insufficient Data";
        } else if (currentOrders > previousOrders
                && currentUsers.size() > previousUsers.size()) {
            trend = "Gaining Popularity";
        } else if (currentOrders < previousOrders
                && currentUsers.size() < previousUsers.size()) {
            trend = "Losing Popularity";
        } else if (currentOrders > previousOrders
                && currentUsers.size() < previousUsers.size()) {
            trend = "Demand Increasing";
        } else if (currentOrders < previousOrders
                && currentUsers.size() > previousUsers.size()) {
            trend = "Emerging Trend";
        } else {
            trend = "Mixed or Stable";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("restaurantName", restaurantName);
        response.put("location", location);
        response.put("currentPeriodOrders", currentOrders);
        response.put("previousPeriodOrders", previousOrders);
        response.put("currentPeriodUniqueUsers", currentUsers.size());
        response.put("previousPeriodUniqueUsers", previousUsers.size());
        response.put("trend", trend);

        return response;
    }
}