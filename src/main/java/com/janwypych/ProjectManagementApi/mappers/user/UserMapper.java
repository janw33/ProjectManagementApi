package com.janwypych.ProjectManagementApi.mappers.user;

import com.janwypych.ProjectManagementApi.dtos.auth.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.auth.RegisterRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .build();
    }

    public AuthResponse toResponse(String token) {
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
