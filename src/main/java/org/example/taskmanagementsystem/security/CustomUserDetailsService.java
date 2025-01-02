package org.example.taskmanagementsystem.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Replace with actual user retrieval from the database
        if ("user".equals(username)) {
            return new User("user", "{noop}password", new ArrayList<>()); // {noop} means no password encoding
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
