package com.foodcircle.foodcircle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodcircle.foodcircle.entity.User;
import com.foodcircle.foodcircle.repository.UserRepository;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
    return userRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user) {

    if (user.getName() == null || user.getName().isBlank()
            || user.getEmail() == null || user.getEmail().isBlank()
            || user.getPassword() == null || user.getPassword().isBlank()) {

        return ResponseEntity.badRequest().body("All fields are compulsory");
    }

    if (userRepository.existsByEmail(user.getEmail().trim())) {
        return ResponseEntity.status(409).body("Already a user");
    }

    user.setEmail(user.getEmail().trim());

   User savedUser = userRepository.save(user);

    return ResponseEntity.status(201).body(
    java.util.Map.of(
        "id", savedUser.getId(),
        "name", savedUser.getName(),
        "email", savedUser.getEmail()
    )
    );
}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
    User user = userRepository.findByEmailAndPassword(
        loginUser.getEmail(),
        loginUser.getPassword()
    );

    if (user == null) {
        throw new RuntimeException("User not found");
    }

    return ResponseEntity.ok(
    java.util.Map.of(
        "id", user.getId(),
        "name", user.getName(),
        "email", user.getEmail()
    )
);
    }

}