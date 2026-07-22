package com.janwypych.ProjectManagementApi.services.workspace;

import com.janwypych.ProjectManagementApi.BaseTest.workspace.BaseTestWorkspace;
import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.UpdateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.workspace.WorkspaceMapper;
import com.janwypych.ProjectManagementApi.repositories.workspace.WorkspaceRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteWorkspaceTests extends BaseTestWorkspace {
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
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> workspaceService.deleteWorkspace(user ,workspace.getId())
        );

        verify(workspaceRepository, never()).delete(any(Workspace.class));

    }

    @Test
    public void shouldDeleteWorkspace() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        workspaceService.deleteWorkspace(user, workspace.getId());

        verify(workspaceRepository).delete(workspace);
    }
}
