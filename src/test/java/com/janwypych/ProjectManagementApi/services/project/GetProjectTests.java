package com.janwypych.ProjectManagementApi.services.project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.project.ProjectDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetProjectTests {
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
        Project project = TestDataUtil.project(workspace);

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> projectService.getProject(user, workspace.getId(), project.getId())
        );
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.getProject(user, workspace.getId(), project.getId())
        );
    }

    @Test
    public void shouldReturnProjectDetailsWhenProjectAndWorkspaceAreFound() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();
        Project project = TestDataUtil.project(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        ProjectDetailsResponse result = projectService.getProject(user, workspace.getId(), project.getId());

        assertEquals(project.getId() ,result.getId());
        assertEquals(project.getName() ,result.getName());
        assertEquals(project.getDescription() ,result.getDescription());
        assertEquals(project.getCreatedAt() ,result.getCreatedAt());
        assertEquals(project.getUpdatedAt() ,result.getUpdatedAt());
    }
}
