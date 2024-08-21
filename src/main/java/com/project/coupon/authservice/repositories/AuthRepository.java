package com.project.coupon.authservice.repositories;

import com.project.coupon.authservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User save(User user);
    Optional<User> findByEmail(String email);
}
