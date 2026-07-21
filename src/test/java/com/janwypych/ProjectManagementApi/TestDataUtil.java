package com.janwypych.ProjectManagementApi;

import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.dtos.comment.UpdateCommentRequest;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.CreateInvitationRequest;
import com.janwypych.ProjectManagementApi.dtos.project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.project.UpdateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.auth.LoginRequest;
import com.janwypych.ProjectManagementApi.dtos.auth.RegisterRequest;
import com.janwypych.ProjectManagementApi.dtos.projectMember.CreateProjectMemberRequest;
import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.dtos.task.UpdateTaskRequest;
import com.janwypych.ProjectManagementApi.dtos.user.UpdateCurrentUserRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.UpdateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.UpdateWorkspaceMemberRequest;
import com.janwypych.ProjectManagementApi.entities.comment.Comment;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.enums.TaskStatus;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
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

    public static User user2() {
        return User.builder()
                .id(2L)
                .username("test1")
                .email("test1@email.com")
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
                .updatedAt(LocalDateTime.now())
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

    public static Project project(Workspace workspace) {
        return Project.builder()
                .id(1L)
                .name("test")
                .description("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .workspace(workspace)
                .build();
    }

    public static WorkspaceMember workspaceMember(User user, Workspace workspace) {
        return WorkspaceMember.builder()
                .id(1L)
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public static WorkspaceMember workspaceMember2(User user2, Workspace workspace) {
        return WorkspaceMember.builder()
                .id(1L)
                .user(user2)
                .workspace(workspace)
                .role(WorkspaceRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public static UpdateProjectRequest updateProjectRequest() {
        return UpdateProjectRequest.builder()
                .name("test1")
                .description("test1")
                .build();
    }

    public static CreateTaskRequest createTaskRequest() {
        return CreateTaskRequest.builder()
                .name("test")
                .description("test")
                .assigneeProjectMemberId(1L)
                .build();
    }

    public static ProjectMember projectMember(WorkspaceMember workspaceMember, Project project) {
        return ProjectMember.builder()
                .id(1L)
                .joinedAt(LocalDateTime.now())
                .workspaceMember(workspaceMember)
                .project(project)
                .build();
    }

    public static ProjectMember projectMember2(WorkspaceMember workspaceMember2, Project project) {
        return ProjectMember.builder()
                .id(2L)
                .joinedAt(LocalDateTime.now())
                .workspaceMember(workspaceMember2)
                .project(project)
                .build();
    }

    public static Task task(ProjectMember projectMember, Project project) {
        return Task.builder()
                .id(1L)
                .name("test")
                .description("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(TaskStatus.NOT_STARTED)
                .assignee(projectMember)
                .project(project)
                .build();
    }

    public static UpdateTaskRequest updateTaskRequest() {
        return UpdateTaskRequest.builder()
                .name("test1")
                .description("test1")
                .status(TaskStatus.DONE)
                .assigneeProjectMemberId(2L)
                .build();
    }


    public static CreateCommentRequest createCommentRequest() {
        return new CreateCommentRequest("test");
    }

    public static Comment comment(ProjectMember projectMember, Task task) {
        LocalDateTime now = LocalDateTime.now();

        return Comment.builder()
                .id(1L)
                .content("test")
                .createdAt(now)
                .updatedAt(now)
                .author(projectMember)
                .task(task)
                .build();
    }

    public static UpdateCommentRequest updateCommentRequest() {
        return UpdateCommentRequest.builder()
                .content("test1")
                .build();
    }

    public static UpdateWorkspaceMemberRequest updateWorkspaceMemberRequest() {
        return new UpdateWorkspaceMemberRequest(WorkspaceRole.MANAGER);
    }

    public static CreateProjectMemberRequest createProjectMemberRequest() {
        return new CreateProjectMemberRequest(2L);
    }

    public static CreateInvitationRequest createInvitationRequest() {
        return CreateInvitationRequest.builder()
                .receiverUserId(2L)
                .build();
    }

    public static Invitation invitationPending(WorkspaceMember member, Workspace workspace, User user2) {
        return Invitation.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .status(InvitationStatus.PENDING)
                .workspace(workspace)
                .expiresAt(LocalDateTime.now())
                .senderWorkspaceMember(member)
                .receiverUser(user2)
                .build();
    }

    public static UpdateCurrentUserRequest updateCurrentUserRequest() {
        return UpdateCurrentUserRequest.builder()
                .username("test1")
                .email("test1@email.com")
                .build();
    }
}
