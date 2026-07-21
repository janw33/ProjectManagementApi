package com.janwypych.ProjectManagementApi.controllers.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.dtos.user.UserResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetCurrentUserTests extends BaseTestUser {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                receiverUser,
                null,
                null
        );
    }

    private RequestPostProcessor authenticatedUser() {
        return authentication(createAuthentication());
    }

    private void performGet(ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/account")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/account"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturn200WhenRequestIsValid() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(receiverUser.getId())
                .username(receiverUser.getUsername())
                .email(receiverUser.getEmail())
                .createdAt(receiverUser.getCreatedAt())
                .build();

        when(userService.getCurrentUser(any(User.class)))
                .thenReturn(userResponse);

        performGet(
                status().isOk(),
                jsonPath("$.id").value(receiverUser.getId()),
                jsonPath("$.username").value(receiverUser.getUsername()),
                jsonPath("$.email").value(receiverUser.getEmail()),
                jsonPath("$.createdAt").isNotEmpty());
    }
}