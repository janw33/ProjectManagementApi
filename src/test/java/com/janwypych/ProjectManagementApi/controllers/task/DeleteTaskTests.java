package com.janwypych.ProjectManagementApi.controllers.task;

import com.janwypych.ProjectManagementApi.BaseTest.task.BaseTestTask;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteTaskTests extends BaseTestTask {
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

    private void performDelete(Long workspaceId, Long projectId, Long taskId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}", workspaceId, projectId, taskId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/workspaces/1/projects/1/tasks/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(taskService).deleteTask(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()));

        performDelete(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        doThrow(new ProjectNotFoundException())
                .when(taskService).deleteTask(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()));

        performDelete(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp404WhenTaskIsNotFound() throws Exception {
        doThrow(new TaskNotFoundException())
                .when(taskService).deleteTask(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()));

        performDelete(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("TASK_NOT_FOUND"),
                jsonPath("$.message").value("Task not found"));
    }


    @Test
    public void shouldReturnHttp204WhenRequestIsValid() throws Exception {
        performDelete(workspace.getId(), project.getId(), task.getId(),
                status().isNoContent());
    }
}
