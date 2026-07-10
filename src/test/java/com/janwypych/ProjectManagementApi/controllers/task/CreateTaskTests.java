package com.janwypych.ProjectManagementApi.controllers.task;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.Project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.dtos.task.TaskIdResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.project.ProjectService;
import com.janwypych.ProjectManagementApi.services.task.BaseTestTask;
import com.janwypych.ProjectManagementApi.services.task.TaskService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateTaskTests extends BaseTestTask {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

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

    private void performCreate(Long workspaceId, Long projectId, CreateTaskRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks", workspaceId, projectId)
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
        String requestJson = objectMapper.writeValueAsString(createTaskRequest);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/workspaces/1/projects/1/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp400WhenNameIsNull() throws Exception {
        createTaskRequest.setName(null);
        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsBlank() throws Exception {
        createTaskRequest.setName("   ");
        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooShort() throws Exception {
        createTaskRequest.setName("a");
        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooLong() throws Exception {
        createTaskRequest.setName("a".repeat(101));
        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionIsTooLong() throws Exception {
        createTaskRequest.setDescription("a".repeat(2001));
        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be at most 2000 characters long"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionContainsOnlyWhitespace() throws Exception {
        createTaskRequest.setDescription("    ");
        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot contain only whitespace"));
    }

    @Test
    public void shouldReturnHttp400WhenAssigneeProjectMemberIdIsNull() throws Exception {
        createTaskRequest.setAssigneeProjectMemberId(null);
        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.assigneeProjectMemberId").value("Assignee id cannot be null"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(taskService.createTask(any(User.class), eq(createTaskRequest), eq(workspace.getId()), eq(project.getId())))
                .thenThrow(new WorkspaceNotFoundException());

        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        when(taskService.createTask(any(User.class), eq(createTaskRequest), eq(workspace.getId()), eq(project.getId())))
                .thenThrow(new ProjectNotFoundException());

        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectMemberIsNotFound() throws Exception {
        when(taskService.createTask(any(User.class), eq(createTaskRequest), eq(workspace.getId()), eq(project.getId())))
                .thenThrow(new ProjectMemberNotFoundException());

        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Project member not found"));
    }

    @Test
    public void shouldReturnHttp201WhenRequestIsValid() throws Exception {
        when(taskService.createTask(any(User.class), eq(createTaskRequest), eq(workspace.getId()), eq(project.getId())))
                .thenReturn(new TaskIdResponse(1L));

        performCreate(workspace.getId(), project.getId(), createTaskRequest,
                status().isCreated(),
                jsonPath("$.id").value(1L));
    }
}
