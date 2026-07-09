package com.janwypych.ProjectManagementApi.services.Project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.*;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetProjectsTests {
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

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
              WorkspaceNotFoundException.class,
                () -> projectService.getProjects(user, workspace.getId(), Pageable.unpaged())
        );
    }

    @Test
    public void shouldGetProjectsWhenWorkspaceIsFoundAndMemberIsOwner() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.OWNER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        Page<Project> projects = new PageImpl<>(List.of(TestDataUtil.project()));

        when(projectRepository.findAllByWorkspace(workspace, Pageable.unpaged()))
                .thenReturn(projects);

        when(projectMemberRepository.findAllByUserAndProject_Workspace(user, workspace))
                .thenReturn(List.of());

        Page<ProjectSummaryResponse> result = projectService.getProjects(user, workspace.getId(), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(projects.getContent().getFirst().getId() ,result.getContent().getFirst().getId());
        assertEquals(projects.getContent().getFirst().getName() ,result.getContent().getFirst().getName());
        assertTrue(result.getContent().getFirst().isHasAccess());

    }

    @Test
    public void shouldGetProjectsWhenWorkspaceIsFoundAndMemberIsManager() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.MANAGER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        Page<Project> projects = new PageImpl<>(List.of(TestDataUtil.project()));

        when(projectRepository.findAllByWorkspace(workspace, Pageable.unpaged()))
                .thenReturn(projects);

        when(projectMemberRepository.findAllByUserAndProject_Workspace(user, workspace))
                .thenReturn(List.of());

        Page<ProjectSummaryResponse> result = projectService.getProjects(user, workspace.getId(), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(projects.getContent().getFirst().getId() ,result.getContent().getFirst().getId());
        assertEquals(projects.getContent().getFirst().getName() ,result.getContent().getFirst().getName());
        assertTrue(result.getContent().getFirst().isHasAccess());
    }

    @Test
    public void shouldGetProjectsWithNoAccessWhenWorkspaceIsFoundAndMemberIsNotProjectMember() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.MEMBER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        Page<Project> projects = new PageImpl<>(List.of(TestDataUtil.project()));

        when(projectRepository.findAllByWorkspace(workspace, Pageable.unpaged()))
                .thenReturn(projects);

        when(projectMemberRepository.findAllByUserAndProject_Workspace(user, workspace))
                .thenReturn(List.of());

        Page<ProjectSummaryResponse> result = projectService.getProjects(user, workspace.getId(), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(projects.getContent().getFirst().getId() ,result.getContent().getFirst().getId());
        assertEquals(projects.getContent().getFirst().getName() ,result.getContent().getFirst().getName());
        assertFalse(result.getContent().getFirst().isHasAccess());
    }

    @Test
    public void shouldGetProjectsWithAccessWhenMemberBelongsToProject() {
        User user = TestDataUtil.user();
        Workspace workspace = TestDataUtil.workspace();

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(WorkspaceRole.MEMBER)
                .build();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        Project project = TestDataUtil.project();

        Page<Project> projects = new PageImpl<>(List.of(project));

        when(projectRepository.findAllByWorkspace(workspace, Pageable.unpaged()))
                .thenReturn(projects);

        ProjectMember projectMember = ProjectMember.builder()
                .id(1L)
                .project(project)
                .joinedAt(LocalDateTime.now())
                .user(user)
                .build();

        when(projectMemberRepository.findAllByUserAndProject_Workspace(user, workspace))
                .thenReturn(List.of(projectMember));

        Page<ProjectSummaryResponse> result = projectService.getProjects(user, workspace.getId(), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(projects.getContent().getFirst().getId() ,result.getContent().getFirst().getId());
        assertEquals(projects.getContent().getFirst().getName() ,result.getContent().getFirst().getName());
        assertTrue(result.getContent().getFirst().isHasAccess());
    }
}
