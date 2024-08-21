package com.project.coupon.security.services;

import com.project.coupon.authservice.models.Token;
import com.project.coupon.authservice.models.User;
import com.project.coupon.authservice.repositories.AuthRepository;
import com.project.coupon.authservice.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Log4j2
public class JwtImplementation implements JwtService {
    @Autowired
    private TokenRepository tokenRepository;

    private SecretKey key;
    private long jwtExpirationInYears = 5;
    private static final Logger logger = Logger.getLogger(JwtImplementation.class.getName());

    @Autowired
    private AuthRepository authRepository;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public void setJwtExpirationInMs(int jwtExpirationInYears) {
        this.jwtExpirationInYears = jwtExpirationInYears;
        logger.info("JWT Expiration in years: " + jwtExpirationInYears);
    }

    public String generateToken(UserDetails userDetails) {
        // Fetch User entity using username
        User user = authRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername())
        );

        // Extract roles
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId()); // Store user_id as Long
        claims.put("roles", roles);

        long yearInMs = 365L * 24 * 60 * 60 * 1000; // 1 year in milliseconds
        long expirationTimeInMs = jwtExpirationInYears * yearInMs;

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeInMs);


        logger.info("Current Time: " + now);
        logger.info("Expiration Time: " + expiryDate);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenEntity.setActive(true);
        tokenEntity.setExpiryAt(expiryDate);
        tokenRepository.save(tokenEntity);

        return token;
    }



    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long fiveYearsInMilliseconds = 5L * 365 * 24 * 60 * 60 * 1000;
        String refreshToken = Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +fiveYearsInMilliseconds ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        logger.info("Generated Refresh Token: " + refreshToken);
        return refreshToken;
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        logger.info("Extracted Claims: " + claims);
        return claims;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Optional<Token> userSessionEntity = tokenRepository.findByTokenAndIsActive(token, true);
        final String username = extractUserName(token);
        boolean isValid = (userSessionEntity.isPresent() && username.equals(userDetails.getUsername()) && !isTokenExpired(token));

        Claims claims = extractAllClaims(token);
        Long userId = claims.get("user_id", Long.class); // Retrieve user_id as Long
        List<String> roles = claims.get("roles", List.class);

        // Add additional validation for user_id and roles if necessary

        logger.info("Is Token Valid: " + isValid);
        return isValid;
    }



    private boolean isTokenExpired(String token) {
        boolean isExpired = extractClaim(token, Claims::getExpiration).before(new Date());
        logger.info("Is Token Expired: " + isExpired);
        return isExpired;
    }
}