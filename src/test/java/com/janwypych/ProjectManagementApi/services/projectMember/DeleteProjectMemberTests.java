package com.janwypych.ProjectManagementApi.services.projectMember;

import com.janwypych.ProjectManagementApi.BaseTest.projectMember.BaseTestProjectMember;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberAlreadyDeletedException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.projectMember.ProjectMemberMapper;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteProjectMemberTests extends BaseTestProjectMember {
    private final ProjectMemberMapper projectMemberMapper = new ProjectMemberMapper();

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private ProjectMemberService projectMemberService;

    @BeforeEach
    void setUp() {
        projectMemberService = new ProjectMemberService(
                workspaceMemberRepository,
                projectRepository,
                projectMemberRepository,
                projectMemberMapper
        );
    }

    @Test
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> projectMemberService.deleteProjectMember(user, workspace.getId(), project.getId(), projectMember.getId())
        );

        assertTrue(projectMember.isActive());
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectMemberService.deleteProjectMember(user, workspace.getId(), project.getId(), projectMember.getId())
        );

        assertTrue(projectMember.isActive());
    }

    @Test
    public void shouldThrowProjectMemberNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.findByIdAndProject(projectMember.getId(), project))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectMemberNotFoundException.class,
                () -> projectMemberService.deleteProjectMember(user, workspace.getId(), project.getId(), projectMember.getId())
        );

        assertTrue(projectMember.isActive());
    }


    @Test
    public void shouldThrowProjectMemberAlreadyDeletedException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.findByIdAndProject(projectMember.getId(), project))
                .thenReturn(Optional.of(projectMember));

        projectMember.setActive(false);

        assertThrows(
                ProjectMemberAlreadyDeletedException.class,
                () -> projectMemberService.deleteProjectMember(user, workspace.getId(), project.getId(), projectMember.getId())
        );

        assertFalse(projectMember.isActive());
    }

    @Test
    public void shouldDeleteProjectMember() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.findByIdAndProject(projectMember.getId(), project))
                .thenReturn(Optional.of(projectMember));

        projectMemberService.deleteProjectMember(user, workspace.getId(), project.getId(), projectMember.getId());

        assertFalse(projectMember.isActive());
    }
}