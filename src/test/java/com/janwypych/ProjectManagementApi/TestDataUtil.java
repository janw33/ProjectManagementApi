package com.janwypych.ProjectManagementApi;

import com.janwypych.ProjectManagementApi.dtos.Project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectSummaryResponse;
import com.janwypych.ProjectManagementApi.dtos.auth.LoginRequest;
import com.janwypych.ProjectManagementApi.dtos.auth.RegisterRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.UpdateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.Project;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;

import java.time.LocalDateTime;

public final class TestDataUtil {
    public static RegisterRequest createUserRequest() {
        return RegisterRequest.builder()
                .username("test")
                .email("test@email.com")
                .password("testPassword")
                .build();
    }
    public static User user() {
        return User.builder()
                .id(1L)
                .username("test")
                .email("test@email.com")
                .password("testPassword")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static LoginRequest loginRequest() {
        return LoginRequest.builder()
                .email("test@email.com")
                .password("testPassword")
                .build();
    }

    public static CreateWorkspaceRequest createWorkspaceRequest() {
        return CreateWorkspaceRequest.builder()
                .name("test")
                .description("test")
                .build();
    }

    public static Workspace workspace() {
        return Workspace.builder()
                .id(1L)
                .name("test")
                .description("test")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static WorkspaceDetailsResponse workspaceDetailsResponse() {
        return WorkspaceDetailsResponse.builder()
                .id(1L)
                .name("test")
                .description("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(WorkspaceRole.OWNER)
                .build();
    }

    public static UpdateWorkspaceRequest updateWorkspaceRequest() {
        return UpdateWorkspaceRequest.builder()
                .name("test1")
                .description("test1")
                .build();
    }

    public static CreateProjectRequest createProjectRequest() {
        return CreateProjectRequest.builder()
                .name("test")
                .description("test")
                .build();
    }

    public static Project project() {
        return Project.builder()
                .id(1L)
                .name("test")
                .description("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


}
