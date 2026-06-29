package com.janwypych.ProjectManagementApi;

import com.janwypych.ProjectManagementApi.dtos.CreateUserRequest;

public final class TestDataUtil {
    public static CreateUserRequest createAccountRequest() {
        return new CreateUserRequest("test", "test@email.com", "testPassword");
    }
}
