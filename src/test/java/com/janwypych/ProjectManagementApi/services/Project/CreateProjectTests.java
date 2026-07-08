package com.janwypych.ProjectManagementApi.services.Project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.Project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.entities.Project;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private WorkspaceMemberRepository workspaceMemberRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(
                workspaceMemberRepository,
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
