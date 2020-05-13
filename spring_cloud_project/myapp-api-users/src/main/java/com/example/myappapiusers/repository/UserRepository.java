package com.example.myappapiusers.repository;

import com.example.myappapiusers.data.User1Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User1Entity, Long> {
    Optional<User1Entity> findByFirstName(String firstName);
    Optional<User1Entity> findByEmail(String email);
}
