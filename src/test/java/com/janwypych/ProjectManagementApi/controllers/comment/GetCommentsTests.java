package com.janwypych.ProjectManagementApi.controllers.comment;

import com.janwypych.ProjectManagementApi.BaseTest.comment.BaseTestComment;
import com.janwypych.ProjectManagementApi.dtos.comment.CommentResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetCommentsTests extends BaseTestComment {
    @Autowired
    private MockMvc mockMvc;

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

    private void performGet(Long workspaceId, Long projectId, Long taskId, ResultMatcher... matchers) throws Exception {

        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments", workspaceId, projectId, taskId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1/projects/1/tasks/1/comments"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(commentService.getComments(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId()), any(Pageable.class)))
                .thenThrow(new WorkspaceNotFoundException());

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        when(commentService.getComments(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId()), any(Pageable.class)))
                .thenThrow(new ProjectNotFoundException());

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectMemberIsNotFound() throws Exception {
        when(commentService.getComments(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId()), any(Pageable.class)))
                .thenThrow(new ProjectMemberNotFoundException());

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Project member not found"));
    }

    @Test
    public void shouldReturnHttp404WhenTaskIsNotFound() throws Exception {
        when(commentService.getComments(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId()), any(Pageable.class)))
                .thenThrow(new TaskNotFoundException());

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("TASK_NOT_FOUND"),
                jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        CommentResponse commentResponse = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .edited(false)
                .authorUsername(user.getUsername())
                .build();

        when(commentService.getComments(any(User.class), eq(workspace.getId()), eq(project.getId()), eq(task.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(commentResponse)));

        performGet(workspace.getId(), project.getId(), task.getId(),
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(comment.getId()),
                jsonPath("$.content[0].content").value(comment.getContent()),
                jsonPath("$.content[0].createdAt").isNotEmpty(),
                jsonPath("$.content[0].edited").value(false),
                jsonPath("$.content[0].authorUsername").value(user.getUsername()));
    }
}
