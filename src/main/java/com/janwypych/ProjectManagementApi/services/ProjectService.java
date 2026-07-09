package com.janwypych.ProjectManagementApi.services;

import com.janwypych.ProjectManagementApi.dtos.Project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.*;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(WorkspaceMemberRepository workspaceMemberRepository, ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    public ProjectIdResponse createProject(User currentUser, CreateProjectRequest createProjectRequest, Long workspaceId) {
        Workspace workspace = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"))
                .getWorkspace();

        Project project = projectMapper.toEntity(createProjectRequest, workspace);

        project = projectRepository.save(project);

        return projectMapper.toIdResponse(project);
    }

    public Page<ProjectSummaryResponse> getProjects(User currentUser, Long workspaceId, Pageable pageable) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Page<Project> projects = projectRepository
                .findAllByWorkspace(workspace, pageable);

        boolean hasGlobalAccess =
                member.getRole() == WorkspaceRole.OWNER
                        || member.getRole() == WorkspaceRole.MANAGER;

        Set<Long> accessibleProjects = projectMemberRepository
                .findAllByUserAndProject_Workspace(currentUser, workspace)
                .stream()
                .map(pm -> pm.getProject().getId())
                .collect(Collectors.toSet());

        return projects.map(project -> {

            boolean hasAccess = hasGlobalAccess
                    || accessibleProjects.contains(project.getId());

            return projectMapper.toSummaryResponse(project, hasAccess);
        });
    }
}
