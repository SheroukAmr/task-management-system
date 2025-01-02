package org.example.taskmanagementsystem.controller;

import org.example.taskmanagementsystem.model.JwtResponse;
import org.example.taskmanagementsystem.model.LoginRequest;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.example.taskmanagementsystem.service.UserService;
import org.example.taskmanagementsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/taskManagement")
public class AuthController {
    private final JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!loginRequest.getPassword().equals(user.getUserPassword()) ) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUserName());
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,token).body("Logged in successfully");

    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Use UserService to register the user
        String result = userService.registerUser(user);
        if (result.equals("Username is already taken")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        if (result.equals("Password cannot be null or empty")) {
            return ResponseEntity.badRequest().body(result);
        }
        if (user.getRole() == null) {
            user.setRole("user");
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
