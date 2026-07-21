package com.janwypych.ProjectManagementApi.services.project;

import com.janwypych.ProjectManagementApi.BaseTest.project.BaseTestProject;
import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.project.UpdateProjectRequest;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.project.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
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
public class DeleteProjectTests extends BaseTestProject {
    private final ProjectMapper projectMapper = new ProjectMapper();

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(
                workspaceMemberRepository,
                projectMemberRepository,
                projectRepository,
                projectMapper
        );
    }

    @Test
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> projectService.deleteProject(user, workspace.getId(), project.getId())
        );

        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.deleteProject(user, workspace.getId(), project.getId())
        );

        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    public void shouldDeleteProject() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        projectService.deleteProject(user, workspace.getId(), project.getId());

        verify(projectRepository).delete(project);
    }
}
