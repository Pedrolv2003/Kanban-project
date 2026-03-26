package com.kanban.controller;

import com.kanban.dto.LoginRequest;
import com.kanban.dto.LoginResponse;
import com.kanban.dto.RegisterRequest;
import com.kanban.model.User;
import com.kanban.security.JwtTokenProvider;
import com.kanban.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());
        
        if (user == null || !userService.checkPassword(user, loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        }
        
        String token = tokenProvider.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRole()));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(400).body("Email já cadastrado");
        }
        
        User user = userService.createUser(registerRequest.getEmail(), registerRequest.getPassword(), "USER");
        String token = tokenProvider.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRole()));
    }
}