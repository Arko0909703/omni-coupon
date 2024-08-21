package com.project.coupon.authservice.services;


import com.project.coupon.authservice.configs.EncryptionUtil;
import com.project.coupon.authservice.dtos.LogoutResponseDto;
import com.project.coupon.authservice.dtos.SignUpRequestDto;
import com.project.coupon.authservice.models.Token;
import com.project.coupon.authservice.models.TokenStatus;
import com.project.coupon.authservice.models.User;
import com.project.coupon.authservice.repositories.AuthRepository;
import com.project.coupon.authservice.repositories.TokenRepository;
import com.project.coupon.security.services.JwtImplementation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.support.SessionStatus;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AuthService {

    private AuthRepository authRepository;
    private TokenRepository tokenRepository;

    private EncryptionUtil encryptionUtil;
    JwtImplementation jwtService;

    public AuthService(AuthRepository authRepository,TokenRepository tokenRepository,EncryptionUtil encryptionUtil){
        this.authRepository = authRepository;
        this.tokenRepository = tokenRepository;
        this.encryptionUtil = encryptionUtil;
    }

    public void register(SignUpRequestDto user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setHashedPassword(hashPassword(user.getPassword()));
        authRepository.save(newUser);
    }
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash); // Use Base64 here to store hash as string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occurred while hashing password", e);
        }
    }

}


