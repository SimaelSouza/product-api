package com.example.products.controllers;

import com.example.products.dtos.AuthResponseDto;
import com.example.products.dtos.LoginDto;
import com.example.products.dtos.RegisterDto;
import com.example.products.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDto(authService.register(dto)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginDto dto) {
        return ResponseEntity.ok(new AuthResponseDto(authService.login(dto)));
    }
}
