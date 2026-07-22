package com.janwypych.ProjectManagementApi.services.workspaceMembers;

import com.janwypych.ProjectManagementApi.BaseTest.workspaceMember.BaseTestWorkspaceMembers;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberAlreadyDeletedException;
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
public class DeleteWorkspaceMemberTests extends BaseTestWorkspaceMembers {
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
                () -> workspaceMemberService.deleteWorkspaceMember(user ,workspace.getId(), workspaceMember2.getId())
        );

        assertTrue(workspaceMember2.isActive());
    }

    @Test
    public void shouldThrowWorkspaceMemberNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(workspaceMemberRepository.findByIdAndWorkspace(workspaceMember2.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceMemberNotFoundException.class,
                () -> workspaceMemberService.deleteWorkspaceMember(user ,workspace.getId(), workspaceMember2.getId())
        );

        assertTrue(workspaceMember2.isActive());
    }

    @Test
    public void shouldThrowWorkspaceMemberAlreadyDeletedException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(workspaceMemberRepository.findByIdAndWorkspace(workspaceMember2.getId(), workspace))
                .thenReturn(Optional.of(workspaceMember2));

        workspaceMember2.setActive(false);

        assertThrows(
                WorkspaceMemberAlreadyDeletedException.class,
                () -> workspaceMemberService.deleteWorkspaceMember(user ,workspace.getId(), workspaceMember2.getId())
        );

        assertFalse(workspaceMember2.isActive());
    }

    @Test
    public void shouldDeleteWorkspaceMember() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(workspaceMemberRepository.findByIdAndWorkspace(workspaceMember2.getId(), workspace))
                .thenReturn(Optional.of(workspaceMember2));

        workspaceMemberService.deleteWorkspaceMember(user ,workspace.getId(), workspaceMember2.getId());

        assertFalse(workspaceMember2.isActive());
    }
}
