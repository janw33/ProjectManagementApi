package com.janwypych.ProjectManagementApi.services.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.workspace.WorkspaceMapper;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.workspace.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetWorkspaceTests {
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
        Workspace workspace = TestDataUtil.workspace();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> workspaceService.getWorkspace(user, workspace.getId())
        );

        verify(workspaceMemberRepository).findByWorkspaceIdAndUser(workspace.getId(), user);
    }

    @Test
    public void shouldReturnWorkspaceDetailsResponseWhenWorkspaceExists() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();

        WorkspaceMember member = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(member));

        WorkspaceDetailsResponse result = workspaceService.getWorkspace(user, workspace.getId());

        assertEquals(workspace.getId(), result.getId());
        assertEquals(workspace.getName(), result.getName());
        assertEquals(workspace.getDescription(), result.getDescription());
        assertEquals(workspace.getCreatedAt(), result.getCreatedAt());
        assertEquals(workspace.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(member.getRole(), result.getRole());

        verify(workspaceMemberRepository).findByWorkspaceIdAndUser(workspace.getId(), user);

    }
}
