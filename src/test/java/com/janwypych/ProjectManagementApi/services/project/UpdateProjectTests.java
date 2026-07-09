package com.janwypych.ProjectManagementApi.services.project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.dtos.Project.UpdateProjectRequest;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.project.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
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
public class UpdateProjectTests {
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
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project();
        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> projectService.updateProject(user, request, workspace.getId(), project.getId())
        );
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project();
        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        WorkspaceMember member = TestDataUtil.workspaceMember(user, workspace);

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.updateProject(user, request, workspace.getId(), project.getId())
        );
    }

    @Test
    public void shouldUpdateWhenProjectIsFound() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project();
        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        assertNotEquals(request.getName(), project.getName());
        assertNotEquals(request.getDescription(), project.getDescription());

        WorkspaceMember member = TestDataUtil.workspaceMember(user, workspace);

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        ProjectIdResponse result = projectService.updateProject(user, request, workspace.getId(), project.getId());

        assertEquals(project.getId(), result.getId());
        assertEquals(request.getName(), project.getName());
        assertEquals(request.getDescription(), project.getDescription());
    }

    @Test
    public void shouldUpdateOnlyNameWhenDescriptionIsNull() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project();
        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        request.setDescription(null);
        String originalDescription = project.getDescription();

        assertNotEquals(request.getName(), project.getName());
        assertNotEquals(request.getDescription(), project.getDescription());

        WorkspaceMember member = TestDataUtil.workspaceMember(user, workspace);

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        ProjectIdResponse result = projectService.updateProject(user, request, workspace.getId(), project.getId());

        assertEquals(project.getId(), result.getId());
        assertEquals(request.getName(), project.getName());
        assertEquals(originalDescription, project.getDescription());
    }

    @Test
    public void shouldUpdateOnlyDescriptionWhenNameIsNull() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project();
        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        request.setName(null);
        String originalName = project.getName();

        assertNotEquals(request.getName(), project.getName());
        assertNotEquals(request.getDescription(), project.getDescription());

        WorkspaceMember member = TestDataUtil.workspaceMember(user, workspace);

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        ProjectIdResponse result = projectService.updateProject(user, request, workspace.getId(), project.getId());

        assertEquals(project.getId(), result.getId());
        assertEquals(originalName, project.getName());
        assertEquals(request.getDescription(), project.getDescription());
    }

    @Test
    public void shouldBeTheSameWhenNameAndDescriptionAreNull() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project();
        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        request.setName(null);
        request.setDescription(null);
        String originalName = project.getName();
        String originalDescription = project.getDescription();

        assertNotEquals(request.getName(), project.getName());
        assertNotEquals(request.getDescription(), project.getDescription());

        WorkspaceMember member = TestDataUtil.workspaceMember(user, workspace);

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        ProjectIdResponse result = projectService.updateProject(user, request, workspace.getId(), project.getId());

        assertEquals(project.getId(), result.getId());
        assertEquals(originalName, project.getName());
        assertEquals(originalDescription, project.getDescription());
    }
}