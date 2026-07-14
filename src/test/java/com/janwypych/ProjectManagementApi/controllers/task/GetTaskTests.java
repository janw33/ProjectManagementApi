package com.janwypych.ProjectManagementApi.controllers.task;

import com.janwypych.ProjectManagementApi.BaseTest.task.BaseTestTask;
import com.janwypych.ProjectManagementApi.dtos.task.TaskDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.enums.TaskStatus;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.task.TaskNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.task.TaskService;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetTaskTests extends BaseTestTask {
    @Autowired
    private MockMvc mockMvc;

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

    private void performGet(Long workspaceId, Long projectId, Long taskId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}", workspaceId, projectId, taskId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1/projects/1/tasks/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(taskService.getTask(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId())))
                .thenThrow(new WorkspaceNotFoundException());

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        when(taskService.getTask(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId())))
                .thenThrow(new ProjectNotFoundException());

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp404WhenTaskIsNotFound() throws Exception {
        when(taskService.getTask(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId())))
                .thenThrow(new TaskNotFoundException());

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("TASK_NOT_FOUND"),
                jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        TaskDetailsResponse taskDetailsResponse = TaskDetailsResponse.builder()
                .id(1L)
                .name("test")
                .description("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(TaskStatus.NOT_STARTED)
                .assigneeUsername("test")
                .build();

        when(taskService.getTask(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId())))
                .thenReturn(taskDetailsResponse);

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isOk(),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("test"),
                jsonPath("$.description").value("test"),
                jsonPath("$.createdAt").isNotEmpty(),
                jsonPath("$.updatedAt").isNotEmpty(),
                jsonPath("$.status").value("NOT_STARTED"),
                jsonPath("$.assigneeUsername").value("test"));
    }
}
