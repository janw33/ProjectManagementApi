package com.janwypych.ProjectManagementApi.controllers.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.services.WorkspaceService;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateWorkspaceTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkspaceService workspaceService;

    private Authentication createAuthentication() {
        User user = TestDataUtil.user();

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                null
        );
    }

    private RequestPostProcessor authenticatedUser() {
        return authentication(createAuthentication());
    }

    private void performCreate(CreateWorkspaceRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/workspace/create")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }
    @Test
    public void shouldReturnHttp400WhenNameIsNull() throws Exception {
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();
        request.setName(null);
        performCreate(request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot be blank"));
    }


    @Test
    public void shouldReturnHttp400WhenNameIsBlank() throws Exception {
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();
        request.setName("   ");
        performCreate(request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooShort() throws Exception {
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();
        request.setName("a");
        performCreate(request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooLong() throws Exception {
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();
        request.setName("a".repeat(101));
        performCreate(request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }


    @Test
    public void shouldReturnHttp400WhenDescriptionIsTooLong() throws Exception {
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();
        request.setDescription("a".repeat(501));
        performCreate(request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be at most 500 characters long"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionContainsOnlyWhitespace() throws Exception {
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();
        request.setDescription("    ");
        performCreate(request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot contain only whitespace"));
    }

    @Test
    public void shouldReturnHttp201WhenRequestIsValid() throws Exception {
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();

        CreateWorkspaceResponse response = CreateWorkspaceResponse.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        when(workspaceService.createWorkspace(any(User.class), any(CreateWorkspaceRequest.class)))
                .thenReturn(response);

        performCreate(request,
                status().isCreated(),
                jsonPath("$.id").value(1L),
                jsonPath("$.name").value(request.getName()),
                jsonPath("$.description").value(request.getDescription()),
                jsonPath("$.createdAt").exists());
    }
}
