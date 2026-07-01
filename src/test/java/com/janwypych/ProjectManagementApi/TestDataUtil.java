package com.janwypych.ProjectManagementApi;

import com.janwypych.ProjectManagementApi.dtos.RegisterRequest;
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
        return new User(1L, "test", "test@email.com", "testPassword", LocalDateTime.of(1, 1, 1, 1, 1, 1));
    }
}
