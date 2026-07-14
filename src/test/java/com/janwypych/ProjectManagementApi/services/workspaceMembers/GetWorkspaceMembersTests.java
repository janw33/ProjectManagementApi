package com.janwypych.ProjectManagementApi.services.workspaceMembers;

import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberSummaryResponse;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.workspaceMember.WorkspaceMemberMapper;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.services.workspaceMember.WorkspaceMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetWorkspaceMembersTests extends BaseTestWorkspaceMembers {
    private final WorkspaceMemberMapper workspaceMemberMapper = new WorkspaceMemberMapper();

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    private WorkspaceMemberService workspaceMemberService;

    @BeforeEach
    void setUp() {
        workspaceMemberService = new WorkspaceMemberService(
                workspaceMemberRepository,
                workspaceMemberMapper
        );
    }

    @Test
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> workspaceMemberService.getWorkspaceMembers(user, workspace.getId(), Pageable.unpaged())
        );
    }

    @Test
    public void shouldGetWorkspaceMembersWhenWorkspaceExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(workspaceMemberRepository.findAllByWorkspace(workspace, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(workspaceMember)));

        Page<WorkspaceMemberSummaryResponse> result = workspaceMemberService.getWorkspaceMembers(user, workspace.getId(), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(workspaceMember.getId(), result.getContent().getFirst().getId());
        assertEquals(workspaceMember.getUser().getUsername(), result.getContent().getFirst().getUsername());
        assertEquals(workspaceMember.getRole(), result.getContent().getFirst().getRole());


    }
}
