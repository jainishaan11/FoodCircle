package com.foodcircle.foodcircle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodcircle.foodcircle.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByNameAndEmail(String name, String email);
    User findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
    
}