package com.janwypych.ProjectManagementApi.BaseTest.user;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.entities.user.User;
import org.junit.jupiter.api.BeforeEach;

public abstract  class BaseTestUser {
    protected User user;

    @BeforeEach
    void setupBase() {
        user = TestDataUtil.user();
    }
}
