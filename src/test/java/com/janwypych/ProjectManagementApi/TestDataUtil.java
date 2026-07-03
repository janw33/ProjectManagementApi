package com.janwypych.ProjectManagementApi;

import com.janwypych.ProjectManagementApi.dtos.auth.LoginRequest;
import com.janwypych.ProjectManagementApi.dtos.auth.RegisterRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.entities.User;

import java.time.LocalDateTime;

public final class TestDataUtil {
    public static RegisterRequest createUserRequest() {
        return RegisterRequest.builder()
                .username("test")
                .email("test@email.com")
                .password("testPassword")
                .build();
    }
    public static User user() {
        return User.builder()
                .id(1L)
                .username("test")
                .email("test@email.com")
                .password("testPassword")
                .createdAt(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1))
                .build();
    }

    public static LoginRequest loginRequest() {
        return LoginRequest.builder()
                .email("test@email.com")
                .password("testPassword")
                .build();
    }

    public static CreateWorkspaceRequest workspaceRequest() {
        return CreateWorkspaceRequest.builder()
                .name("test")
                .description("test")
                .build();
    }
}
