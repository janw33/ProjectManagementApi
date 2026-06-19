package com.janwypych.ProjectManagementApi.mappers;

import com.janwypych.ProjectManagementApi.dtos.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.CreateUserRequest;
import com.janwypych.ProjectManagementApi.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(CreateUserRequest request, String encodedPassword) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(encodedPassword)
                .build();
    }

    public AuthResponse toResponse(String token) {
        return new AuthResponse(token);
    }
}
