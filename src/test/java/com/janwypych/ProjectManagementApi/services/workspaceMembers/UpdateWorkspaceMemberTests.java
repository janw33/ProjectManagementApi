package com.janwypych.ProjectManagementApi.services.workspaceMembers;

import com.janwypych.ProjectManagementApi.BaseTest.workspaceMember.BaseTestWorkspaceMembers;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.workspaceMember.WorkspaceMemberMapper;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.services.workspaceMember.WorkspaceMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateWorkspaceMemberTests extends BaseTestWorkspaceMembers {
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
                () -> workspaceMemberService.updateWorkspaceMember(user, updateWorkspaceMemberRequest ,workspace.getId(), workspaceMember2.getId())
        );
    }

    @Test
    public void shouldThrowWorkspaceMemberNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(workspaceMemberRepository.findByIdAndWorkspace(workspaceMember2.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceMemberNotFoundException.class,
                () -> workspaceMemberService.updateWorkspaceMember(user, updateWorkspaceMemberRequest, workspace.getId(), workspaceMember2.getId())
        );
    }

    @Test
    public void shouldUpdateWorkspaceMemberWhenHeExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(workspaceMemberRepository.findByIdAndWorkspace(workspaceMember2.getId(), workspace))
                .thenReturn(Optional.of(workspaceMember2));

        assertNotEquals(updateWorkspaceMemberRequest.getRole(), workspaceMember2.getRole());

        workspaceMemberService.updateWorkspaceMember(user, updateWorkspaceMemberRequest, workspace.getId(), workspaceMember2.getId());

        assertEquals(updateWorkspaceMemberRequest.getRole(), workspaceMember2.getRole());
    }

    @Test
    public void shouldNotUpdateWhenRoleIsNull() {
        updateWorkspaceMemberRequest.setRole(null);

        WorkspaceRole originalRole = workspaceMember2.getRole();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(workspaceMemberRepository.findByIdAndWorkspace(workspaceMember2.getId(), workspace))
                .thenReturn(Optional.of(workspaceMember2));

        workspaceMemberService.updateWorkspaceMember(user, updateWorkspaceMemberRequest, workspace.getId(), workspaceMember2.getId());

        assertEquals(originalRole, workspaceMember2.getRole());
    }
}