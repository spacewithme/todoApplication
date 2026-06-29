package com.todo.user_service.service;

import com.todo.user_service.model.User;
import com.todo.user_service.repository.UserRepository;
import com.todo.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, String> register(User user) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.existsByUsername(user.getUsername())) {
            response.put("error", "Username already exists");
            return response;
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("error", "Email already exists");
            return response;
        }

        userRepository.save(user);
        response.put("message", "User registered successfully");
        return response;
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !user.getPassword().equals(password)) {
            response.put("error", "Invalid username or password");
            return response;
        }

        String token = jwtUtil.generateToken(username);
        response.put("token", token);
        return response;
    }

    public Map<String, Object> validateToken(String token) {
        Map<String, Object> response = new HashMap<>();

        if (jwtUtil.validateToken(token)) {
            response.put("valid", true);
            response.put("username", jwtUtil.extractUsername(token));
        } else {
            response.put("valid", false);
        }

        return response;
    }
}