package com.janwypych.ProjectManagementApi.services.projectMember;

import com.janwypych.ProjectManagementApi.BaseTest.projectMember.BaseTestProjectMember;
import com.janwypych.ProjectManagementApi.dtos.projectMember.ProjectMemberResponse;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetProjectMembersTests extends BaseTestProjectMember {
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
                () -> projectMemberService.getProjectMembers(user, workspace.getId(), project.getId(), Pageable.unpaged())
        );
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectMemberService.getProjectMembers(user, workspace.getId(), project.getId(), Pageable.unpaged())
        );
    }

    @Test
    public void shouldGetProjectMembersWhenProjectExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.findAllByProject(project, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(projectMember)));

        Page<ProjectMemberResponse> result = projectMemberService.getProjectMembers(user, workspace.getId(), project.getId(), Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        assertEquals(projectMember.getId(), result.getContent().getFirst().getId());
        assertEquals(projectMember.getWorkspaceMember().getUser().getUsername(), result.getContent().getFirst().getUsername());
        assertEquals(projectMember.getWorkspaceMember().getRole(), result.getContent().getFirst().getWorkspaceRole());
        assertNotNull(result.getContent().getFirst().getJoinedAt());
    }
}
