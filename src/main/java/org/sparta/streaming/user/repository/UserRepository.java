package org.sparta.streaming.user.repository;


import org.sparta.streaming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUseremail(String useremail);
}