package com.janwypych.ProjectManagementApi.controllers.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.user.UpdateCurrentUserRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.auth.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.user.UserService;
import com.janwypych.ProjectManagementApi.services.workspace.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateCurrentUserTests extends BaseTestUser {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                null
        );
    }

    private RequestPostProcessor authenticatedUser() {
        return authentication(createAuthentication());
    }

    private void performUpdate(UpdateCurrentUserRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/account")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);

        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        String requestJson = objectMapper.writeValueAsString(updateCurrentUserRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }
    @Test
    public void shouldReturnHttp400WhenUsernameContainsOnlyWhitespaces() throws Exception {
        updateCurrentUserRequest.setUsername("      ");

        performUpdate(updateCurrentUserRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must contain only letters, numbers and underscores"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameIsTooShort() throws Exception {
        updateCurrentUserRequest.setUsername("a");

        performUpdate(updateCurrentUserRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must be between 3 and 30 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameIsTooLong() throws Exception {
        updateCurrentUserRequest.setUsername("a".repeat(31));

        performUpdate(updateCurrentUserRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must be between 3 and 30 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenUsernameContainSpecialCharacter() throws Exception {
        updateCurrentUserRequest.setUsername("test?");

        performUpdate(updateCurrentUserRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.username").value("Username must contain only letters, numbers and underscores"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsTooLong() throws Exception {
        updateCurrentUserRequest.setEmail("a".repeat(63)+"@" + "a".repeat(33) + ".com");

        performUpdate(updateCurrentUserRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email must be at most 100 characters long"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailIsBadFormat() throws Exception {
        updateCurrentUserRequest.setEmail("aaaa");

        performUpdate(updateCurrentUserRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email must be a valid email address"));
    }

    @Test
    public void shouldReturnHttp400WhenEmailContainsOnlyWhitespaces() throws Exception {
        updateCurrentUserRequest.setEmail("     ");

        performUpdate(updateCurrentUserRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.email").value("Email must be a valid email address"));
    }


    @Test
    public void shouldReturnHttp409WhenUsernameAlreadyExists() throws Exception {
        doThrow(new UsernameAlreadyExistsException())
                .when(userService)
                .updateCurrentUser(
                        any(User.class),
                        eq(updateCurrentUserRequest)
                );

        performUpdate(updateCurrentUserRequest,
                status().isConflict(),
                jsonPath("$.error").value("USERNAME_ALREADY_EXISTS"),
                jsonPath("$.message").value("Username already exists"));
    }

    @Test
    public void shouldReturnHttp409WhenEmailAlreadyExists() throws Exception {
        doThrow(new EmailAlreadyExistsException())
                .when(userService)
                .updateCurrentUser(
                        any(User.class),
                        eq(updateCurrentUserRequest)
                );

        performUpdate(updateCurrentUserRequest,
                status().isConflict(),
                jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"),
                jsonPath("$.message").value("Email already exists"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        performUpdate(updateCurrentUserRequest,
                status().isOk());
    }
}
