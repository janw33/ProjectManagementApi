package com.janwypych.ProjectManagementApi.services.project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.project.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateProjectTests {
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
    public void shouldThrowWorkspaceNotFoundExceptionWhenUserIsNotMember() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        CreateProjectRequest createProjectRequest = TestDataUtil.createProjectRequest();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> projectService.createProject(user, createProjectRequest, workspace.getId())
        );

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void shouldSaveProjectWhenUserIsMember() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        CreateProjectRequest request = TestDataUtil.createProjectRequest();

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        Project savedProject = Project.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .workspace(workspace)
                .build();

        when(projectRepository.save(any(Project.class)))
                .thenReturn(savedProject);

        ProjectIdResponse response =
                projectService.createProject(user, request, workspace.getId());

        assertEquals(savedProject.getId(), response.getId());

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);

        verify(projectRepository).save(captor.capture());

        Project project = captor.getValue();

        assertEquals(request.getName(), project.getName());
        assertEquals(request.getDescription(), project.getDescription());
        assertEquals(workspace, project.getWorkspace());
    }
}
