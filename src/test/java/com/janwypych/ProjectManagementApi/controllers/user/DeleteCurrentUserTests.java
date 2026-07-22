package com.janwypych.ProjectManagementApi.controllers.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.user.UserAlreadyDeletedException;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteCurrentUserTests extends BaseTestUser {
    @Autowired
    private MockMvc mockMvc;

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

    private void performDelete(ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/account")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);

        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/account"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp409WhenUserWasAlreadyDeleted() throws Exception {
        doThrow(new UserAlreadyDeletedException())
                .when(userService)
                .deleteCurrentUser(
                    any(User.class)
                );

        performDelete(
                status().isConflict(),
                jsonPath("$.error").value("USER_ALREADY_DELETED"),
                jsonPath("$.message").value("User has already been deleted"));
    }


    @Test
    public void shouldReturnHttp204WhenRequestIsValid() throws Exception {
        performDelete(
                status().isNoContent());
    }
}
