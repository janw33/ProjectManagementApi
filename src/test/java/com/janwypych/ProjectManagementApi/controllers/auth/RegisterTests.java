package com.janwypych.ProjectManagementApi.controllers.auth;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.RegisterRequest;
import com.janwypych.ProjectManagementApi.exceptions.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.services.AuthService;
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
public class RegisterTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private void performRegister(RegisterRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp400WhenUsernameIsBlank() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setUsername(null);

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameIsTooShort() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setUsername("aa");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must be between 3 and 30 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameIsTooLong() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setUsername("a".repeat(31));

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must be between 3 and 30 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameStartsWith_() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setUsername("_test");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must contain only letters, numbers and underscores"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameEndsWith_() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setUsername("test_");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must contain only letters, numbers and underscores"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameContainsSpecialCharacter() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setUsername("test-test");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must contain only letters, numbers and underscores"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameContainsSpace() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setUsername("test test");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must contain only letters, numbers and underscores"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsBlank() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setEmail(null);

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsTooLong() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setEmail("a".repeat(64) + "@" + "a".repeat(32) + ".com");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email must be at most 100 characters long"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsBadFormat() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setEmail("a".repeat(65) + "@email.com");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email must be a valid email address"));
    }

    @Test
    public void shouldReturnHttp400WhenPasswordIsBlank() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setPassword(null);

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.password").value("Password cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenPasswordIsTooShort() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setPassword("aaaaaaa");

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.password").value("Password must be between 8 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenPasswordIsTooLong() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();
        registerRequest.setPassword("a".repeat(101));

        performRegister(registerRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.password").value("Password must be between 8 and 100 characters"));
    }


    @Test
    public void shouldReturnHttp409WhenUsernameIsUnavailable() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UsernameAlreadyExistsException("Username already exists"));

        performRegister(registerRequest,
                status().isConflict(),
                jsonPath("$.error").value("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    public void shouldReturnHttp409WhenEmailIsUnavailable() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        performRegister(registerRequest,
                status().isConflict(),
                jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    public void shouldReturnHttp201WhenCredentialsAreValid() throws Exception {
        RegisterRequest registerRequest = TestDataUtil.createUserRequest();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("token"));

        performRegister(registerRequest,
                status().isCreated(),
                jsonPath("$.token").value("token"));
    }
}