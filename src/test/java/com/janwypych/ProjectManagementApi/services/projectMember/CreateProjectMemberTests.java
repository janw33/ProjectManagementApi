package com.janwypych.ProjectManagementApi.services.projectMember;

import com.janwypych.ProjectManagementApi.BaseTest.projectMember.BaseTestProjectMember;
import com.janwypych.ProjectManagementApi.entities.comment.Comment;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberNotFoundException;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class CreateProjectMemberTests extends BaseTestProjectMember {
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
                projectMemberRepository
        );
    }

    @Test
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> projectMemberService.createProjectMember(user, createProjectMemberRequest, workspace.getId(), project.getId())
        );

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectMemberService.createProjectMember(user, createProjectMemberRequest, workspace.getId(), project.getId())
        );

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    public void shouldThrowWorkspaceMemberNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(workspaceMemberRepository.findByIdAndWorkspace(createProjectMemberRequest.getWorkspaceMemberId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceMemberNotFoundException.class,
                () -> projectMemberService.createProjectMember(user, createProjectMemberRequest, workspace.getId(), project.getId())
        );

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    public void shouldThrowProjectMemberAlreadyExistsException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(workspaceMemberRepository.findByIdAndWorkspace(createProjectMemberRequest.getWorkspaceMemberId(), workspace))
                .thenReturn(Optional.of(workspaceMember2));

        when(projectMemberRepository.existsByProjectAndWorkspaceMember(project, workspaceMember2))
                .thenReturn(true);

        assertThrows(
                ProjectMemberAlreadyExistsException.class,
                () -> projectMemberService.createProjectMember(user, createProjectMemberRequest, workspace.getId(), project.getId())
        );

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    public void shouldCreateProjectMember() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(workspaceMemberRepository.findByIdAndWorkspace(createProjectMemberRequest.getWorkspaceMemberId(), workspace))
                .thenReturn(Optional.of(workspaceMember2));

        when(projectMemberRepository.existsByProjectAndWorkspaceMember(project, workspaceMember2))
                .thenReturn(false);

        when(projectMemberRepository.save(any(ProjectMember.class)))
                .thenReturn(projectMember);

        projectMemberService.createProjectMember(user, createProjectMemberRequest, workspace.getId(), project.getId());

        ArgumentCaptor<ProjectMember> captor = ArgumentCaptor.forClass(ProjectMember.class);

        verify(projectMemberRepository).save(captor.capture());

        ProjectMember capturedProjectMember = captor.getValue();

        assertEquals(workspaceMember2, capturedProjectMember.getWorkspaceMember());
        assertEquals(project, capturedProjectMember.getProject());
    }
}