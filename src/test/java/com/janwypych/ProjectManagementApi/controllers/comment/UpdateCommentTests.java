package com.janwypych.ProjectManagementApi.controllers.comment;

import com.janwypych.ProjectManagementApi.BaseTest.comment.BaseTestComment;
import com.janwypych.ProjectManagementApi.dtos.comment.UpdateCommentRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.comment.CommentNotFoundException;
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
public class UpdateCommentTests extends BaseTestComment {
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

    private void performUpdate(Long workspaceId, Long projectId, Long taskId, Long commentId, UpdateCommentRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments/{commentId}",
                                workspaceId, projectId, taskId, commentId)
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
        String requestJson = objectMapper.writeValueAsString(updateCommentRequest);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/workspaces/1/projects/1/tasks/1/comments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp400WhenContentContainsOnlyWhitespace() throws Exception {
        updateCommentRequest.setContent(" ");
        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.content").value("Content cannot contain only whitespace"));
    }

    @Test
    public void shouldReturnHttp400WhenContentIsTooLong() throws Exception {
        updateCommentRequest.setContent("a".repeat(2001));
        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.content").value("Content must be at most 2000 characters long"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(commentService)
                .updateComment(
                        any(User.class),
                        eq(updateCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()),
                        eq(comment.getId())
                );

        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        doThrow(new ProjectNotFoundException())
                .when(commentService)
                .updateComment(
                        any(User.class),
                        eq(updateCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()),
                        eq(comment.getId())
                );

        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectMemberIsNotFound() throws Exception {
        doThrow(new ProjectMemberNotFoundException())
                .when(commentService)
                .updateComment(
                        any(User.class),
                        eq(updateCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()),
                        eq(comment.getId())
                );

        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Project member not found"));
    }

    @Test
    public void shouldReturnHttp404WhenTaskIsNotFound() throws Exception {
        doThrow(new TaskNotFoundException())
                .when(commentService)
                .updateComment(
                        any(User.class),
                        eq(updateCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()),
                        eq(comment.getId())
                );

        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("TASK_NOT_FOUND"),
                jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void shouldReturnHttp404WhenCommentIsNotFound() throws Exception {
        doThrow(new CommentNotFoundException())
                .when(commentService)
                .updateComment(
                        any(User.class),
                        eq(updateCommentRequest),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(task.getId()),
                        eq(comment.getId())
                );

        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isNotFound(),
                jsonPath("$.error").value("COMMENT_NOT_FOUND"),
                jsonPath("$.message").value("Comment not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        performUpdate(workspace.getId(), project.getId(), task.getId(), comment.getId(), updateCommentRequest,
                status().isOk());
    }
}
