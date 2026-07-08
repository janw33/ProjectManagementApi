package com.janwypych.ProjectManagementApi.services.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceIdResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.mappers.WorkspaceMapper;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceRepository;
import com.janwypych.ProjectManagementApi.services.WorkspaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateWorkspaceTests {
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
    public void shouldReturnCreateWorkspaceResponse() {
        User currentUser = TestDataUtil.user();
        CreateWorkspaceRequest request = TestDataUtil.workspaceRequest();

        Workspace savedWorkspace = Workspace.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        when(workspaceRepository.save(any(Workspace.class)))
                .thenReturn(savedWorkspace);

        WorkspaceIdResponse result =
                workspaceService.createWorkspace(currentUser, request);

        assertNotNull(result);
        assertEquals(savedWorkspace.getId(), result.getId());

        verify(workspaceRepository).save(any(Workspace.class));

        ArgumentCaptor<WorkspaceMember> captor =
                ArgumentCaptor.forClass(WorkspaceMember.class);

        verify(workspaceMemberRepository).save(captor.capture());

        WorkspaceMember member = captor.getValue();

        assertEquals(currentUser, member.getUser());
        assertEquals(savedWorkspace, member.getWorkspace());
        assertEquals(WorkspaceRole.OWNER, member.getRole());
    }
}
