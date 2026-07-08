package com.janwypych.ProjectManagementApi.services.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceSummaryResponse;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetWorkspacesTests {
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
    public void shouldReturnWorkspaceSummaryResponse() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();

        WorkspaceMember member = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .build();

        Page<WorkspaceMember> page =
                new PageImpl<>(List.of(member));

        when(workspaceMemberRepository.findAllByUser(user, Pageable.unpaged()))
                .thenReturn(page);

        Page<WorkspaceSummaryResponse> result =
                workspaceService.getWorkspaces(user, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(workspace.getId(), result.getContent().getFirst().getId());
        assertEquals(workspace.getName(), result.getContent().getFirst().getName());
        assertEquals(member.getRole(), result.getContent().getFirst().getRole());


        verify(workspaceMemberRepository).findAllByUser(user, Pageable.unpaged());
    }
}
