package com.example.textexpander.controller;

import com.example.textexpander.config.JwtService;
import com.example.textexpander.entity.User;
import com.example.textexpander.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtService jwt;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists"));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        String token = jwt.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User creds) {
        User user = userRepo.findByUsername(creds.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(creds.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        String token = jwt.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }
}

