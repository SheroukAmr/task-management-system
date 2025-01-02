package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String registerUser(User user) {
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            return "Username is already taken";
        }
        if (user.getUserPassword() == null || user.getUserPassword().isEmpty()) {
            return "Password cannot be null or empty";
        }
        user.setUserPassword(user.getUserPassword());
        userRepository.save(user);
        return "User registered successfully";
    }

}