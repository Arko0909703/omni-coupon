package com.project.coupon.authservice.repositories;

import com.project.coupon.authservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token save(Token token);

    Optional<Token> findByTokenAndDeletedEquals(String value, boolean isDeleted);

    Optional<Token> findByTokenAndUserId(String token, Long userId); // Use userId instead of user

    Optional<Token> findByTokenAndIsActive(String token, Boolean isActive);

}
