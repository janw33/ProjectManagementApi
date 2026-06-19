package com.janwypych.ProjectManagementApi.controllers;

import com.janwypych.ProjectManagementApi.dtos.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.CreateUserRequest;
import com.janwypych.ProjectManagementApi.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(createUserRequest));
    }
}
