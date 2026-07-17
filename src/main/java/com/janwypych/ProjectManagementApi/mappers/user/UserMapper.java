package com.janwypych.ProjectManagementApi.mappers.user;

import com.janwypych.ProjectManagementApi.dtos.auth.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.auth.RegisterRequest;
import com.janwypych.ProjectManagementApi.dtos.user.UserResponse;
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

    public AuthResponse toTokenResponse(String token) {
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public UserResponse toResponse(User currentUser) {
        return UserResponse.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .createdAt(currentUser.getCreatedAt())
                .build();
    }
}
