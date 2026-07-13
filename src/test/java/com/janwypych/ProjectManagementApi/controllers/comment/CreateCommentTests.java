package com.janwypych.ProjectManagementApi.controllers.comment;

import com.janwypych.ProjectManagementApi.BaseTestComment;
import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.task.TaskNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.comment.CommentService;
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
public class CreateCommentTests extends BaseTestComment {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

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

    private void performCreate(Long workspaceId, Long projectId, Long taskId, CreateCommentRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments", workspaceId, projectId, taskId)
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
        String requestJson = objectMapper.writeValueAsString(createCommentRequest);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/workspaces/1/projects/1/tasks/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp400WhenContentIsNull() throws Exception {
        createCommentRequest.setContent(null);
        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.content").value("Content cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenContentIsBlank() throws Exception {
        createCommentRequest.setContent(" ");
        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.content").value("Content cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenContentIsTooLong() throws Exception {
        createCommentRequest.setContent("a".repeat(2001));
        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.content").value("Content must be at most 2000 characters long"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(commentService)
                .createComment(
                        any(User.class),
                        eq(createCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId())
                );

        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        doThrow(new ProjectNotFoundException())
                .when(commentService)
                .createComment(
                        any(User.class),
                        eq(createCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId())
                );

        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectMemberIsNotFound() throws Exception {
        doThrow(new ProjectMemberNotFoundException())
                .when(commentService)
                .createComment(
                        any(User.class),
                        eq(createCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId())
                );

        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Project member not found"));
    }

    @Test
    public void shouldReturnHttp404WhenTaskIsNotFound() throws Exception {
        doThrow(new TaskNotFoundException())
                .when(commentService)
                .createComment(
                        any(User.class),
                        eq(createCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId())
                );

        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("TASK_NOT_FOUND"),
                jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void shouldReturnHttp201WhenRequestIsValid() throws Exception {
        performCreate(workspace.getId(), project.getId(), task.getId(), createCommentRequest,
                status().isCreated());
    }
}
