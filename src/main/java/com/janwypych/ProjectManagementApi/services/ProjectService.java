package com.janwypych.ProjectManagementApi.services;

import com.janwypych.ProjectManagementApi.dtos.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.entities.Project;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.ProjectMapper;
import com.janwypych.ProjectManagementApi.repositories.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(WorkspaceMemberRepository workspaceMemberRepository, ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.workspaceMemberRepository = workspaceMemberRepository;
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
}
