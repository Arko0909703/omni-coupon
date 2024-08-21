package com.project.coupon.security.services;


import com.project.coupon.authservice.models.User;
import com.project.coupon.authservice.repositories.AuthRepository;
import com.project.coupon.repository.UsersRepository;
import com.project.coupon.security.models.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    private final AuthRepository authRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<User> userOptional;
                if (isValidEmail(username)) {
                    userOptional = authRepository.findByEmail(username);
                } else {
                    throw new UsernameNotFoundException("User: " + username + " doesn't exist.");
                }
                if (userOptional.isEmpty()) {
                    throw new UsernameNotFoundException("User by name: " + username + " doesn't exist.");
                }
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("USER"));

                return new CustomUserDetails(userOptional.get());
            }

            private boolean isValidEmail(String email) {
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                Pattern pattern = Pattern.compile(emailRegex);
                return pattern.matcher(email).matches();
            }
        };
    }
}
