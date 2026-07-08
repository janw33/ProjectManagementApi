package com.janwypych.ProjectManagementApi.services.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.UpdateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceIdResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.WorkspaceMapper;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceRepository;
import com.janwypych.ProjectManagementApi.services.WorkspaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateWorkspaceTests {
    private final WorkspaceMapper workspaceMapper = new WorkspaceMapper();

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    private WorkspaceService workspaceService;

    @BeforeEach
    void setUp() {
        workspaceService = new WorkspaceService(
                workspaceMapper,
                workspaceRepository,
                workspaceMemberRepository
        );
    }

    @Test
    public void shouldThrowWorkspaceNotFoundExceptionWhenIdIsWrong() {
        User user = TestDataUtil.user();
        UpdateWorkspaceRequest updateWorkspaceRequest = TestDataUtil.updateWorkspaceRequest();
        Workspace workspace = TestDataUtil.workspace();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> workspaceService.updateWorkspace(user, updateWorkspaceRequest ,workspace.getId())
        );

        verify(workspaceMemberRepository).findByWorkspaceIdAndUser(workspace.getId(), user);
    }

    @Test
    public void shouldUpdateWorkspaceWhenUserIsInWorkspace() {
        User user = TestDataUtil.user();
        UpdateWorkspaceRequest updateWorkspaceRequest = TestDataUtil.updateWorkspaceRequest();
        Workspace workspace = TestDataUtil.workspace();

        assertNotEquals(updateWorkspaceRequest.getName(), workspace.getName());
        assertNotEquals(updateWorkspaceRequest.getDescription(), workspace.getDescription());

        WorkspaceMember member = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(member));

        WorkspaceIdResponse result = workspaceService.updateWorkspace(user, updateWorkspaceRequest, workspace.getId());

        assertEquals(result.getId(), workspace.getId());
        assertEquals(updateWorkspaceRequest.getName(), workspace.getName());
        assertEquals(updateWorkspaceRequest.getDescription(), workspace.getDescription());

        verify(workspaceMemberRepository).findByWorkspaceIdAndUser(workspace.getId(), user);
    }
}
