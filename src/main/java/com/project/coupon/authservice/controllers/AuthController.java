package com.project.coupon.authservice.controllers;

import com.project.coupon.authservice.dtos.LoginRequestDto;
import com.project.coupon.authservice.dtos.LogoutResponseDto;
import com.project.coupon.authservice.dtos.SignUpRequestDto;
import com.project.coupon.authservice.dtos.ValidateTokenRequestDto;
import com.project.coupon.authservice.models.TokenStatus;
import com.project.coupon.authservice.services.AuthService;
import com.project.coupon.security.services.JwtImplementation;
import com.project.coupon.security.services.JwtService;
import com.project.coupon.security.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Log4j2
public class AuthController {
    private AuthService authService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<LogoutResponseDto> token(@RequestBody LoginRequestDto request) {
        try {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

            LogoutResponseDto responsePayload = new LogoutResponseDto();
            responsePayload.setToken(token);

            return new ResponseEntity<>(responsePayload, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignUpRequestDto user) {
        try {
            authService.register(user);
            return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}