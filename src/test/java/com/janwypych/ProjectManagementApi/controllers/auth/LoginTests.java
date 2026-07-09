package com.janwypych.ProjectManagementApi.controllers.auth;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.auth.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.auth.LoginRequest;
import com.janwypych.ProjectManagementApi.exceptions.auth.InvalidCredentialsException;
import com.janwypych.ProjectManagementApi.services.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private void performLogin(LoginRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsBlank() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();
        loginRequest.setEmail(null);

        performLogin(loginRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsTooLong() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();
        loginRequest.setEmail("a".repeat(64) + "@" + "a".repeat(32) + ".com");

        performLogin(loginRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email must be at most 100 characters long"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsBadFormat() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();
        loginRequest.setEmail("a".repeat(65) + "@email.com");

        performLogin(loginRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email must be a valid email address"));
    }

    @Test
    public void shouldReturnHttp400WhenPasswordIsBlank() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();
        loginRequest.setPassword(null);

        performLogin(loginRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.password").value("Password cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenPasswordIsTooShort() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();
        loginRequest.setPassword("aaaaaaa");

        performLogin(loginRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.password").value("Password must be between 8 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenPasswordIsTooLong() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();
        loginRequest.setPassword("a".repeat(101));

        performLogin(loginRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.password").value("Password must be between 8 and 100 characters"));
    }


    @Test
    public void shouldReturnHttp401WhenCredentialsAreInvalid() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        performLogin(loginRequest,
                status().isUnauthorized(),
                jsonPath("$.error").value("INVALID_CREDENTIALS"),
                jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    public void shouldReturnHttp200WhenCredentialsAreValid() throws Exception {
        LoginRequest loginRequest = TestDataUtil.loginRequest();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("token"));

        performLogin(loginRequest,
                status().isOk(),
                jsonPath("$.token").value("token"));
    }
}
