package org.sparta.streaming.user.repository;

import java.util.Optional;

import org.sparta.streaming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}