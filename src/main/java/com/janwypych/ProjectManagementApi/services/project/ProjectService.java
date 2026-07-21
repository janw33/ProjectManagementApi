package com.janwypych.ProjectManagementApi.services.project;

import com.janwypych.ProjectManagementApi.dtos.project.*;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.project.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ProjectIdResponse createProject(User currentUser, CreateProjectRequest createProjectRequest, Long workspaceId) {
        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = workspaceMember.getWorkspace();

        Project project = projectMapper.toEntity(createProjectRequest, workspace);

        Project savedProject = projectRepository.save(project);

        ProjectMember projectMember = ProjectMember.builder()
                .project(savedProject)
                .workspaceMember(workspaceMember)
                .build();

        projectMemberRepository.save(projectMember);

        return projectMapper.toIdResponse(savedProject);
    }

    public Page<ProjectSummaryResponse> getProjects(User currentUser, Long workspaceId, Pageable pageable) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Page<Project> projects = projectRepository
                .findAllByWorkspaceOrderByUpdatedAtDesc(workspace, pageable);

        boolean hasGlobalAccess =
                member.getRole() == WorkspaceRole.OWNER
                        || member.getRole() == WorkspaceRole.MANAGER;

        Set<Long> accessibleProjects = projectMemberRepository
                .findAllByWorkspaceMemberAndProject_Workspace(member, workspace)
                .stream()
                .map(pm -> pm.getProject().getId())
                .collect(Collectors.toSet());

        return projects.map(project -> {

            boolean hasAccess = hasGlobalAccess
                    || accessibleProjects.contains(project.getId());

            return projectMapper.toSummaryResponse(project, hasAccess);
        });
    }

    public ProjectDetailsResponse getProject(User currentUser, Long workspaceId, Long projectId) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        return projectMapper.toDetailsResponse(project);
    }

    @Transactional
    public ProjectIdResponse updateProject(User currentUser, UpdateProjectRequest request, Long workspaceId, Long projectId) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        if(request.getName() != null) {
            project.setName(request.getName()); }

        if(request.getDescription() != null) {
            project.setDescription(request.getDescription()); }

        return projectMapper.toIdResponse(project);
    }

    public void deleteProject(User currentUser, Long workspaceId, Long projectId) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        projectRepository.delete(project);
    }
}
