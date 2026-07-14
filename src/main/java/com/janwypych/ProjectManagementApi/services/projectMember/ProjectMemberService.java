package com.janwypych.ProjectManagementApi.services.projectMember;

import com.janwypych.ProjectManagementApi.dtos.projectMember.CreateProjectMemberRequest;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberNotFoundException;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectMemberService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberService(WorkspaceMemberRepository workspaceMemberRepository, ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional
    public void createProjectMember(User currentUser, CreateProjectMemberRequest request, Long workspaceId, Long projectId) {
        WorkspaceMember currentWorkspaceMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = currentWorkspaceMember.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        WorkspaceMember targetWorkspaceMember = workspaceMemberRepository
                .findByIdAndWorkspace(request.getWorkspaceMemberId(), workspace)
                .orElseThrow(WorkspaceMemberNotFoundException::new);

        ProjectMember targetProjectMember = ProjectMember.builder()
                .workspaceMember(targetWorkspaceMember)
                .project(project)
                .build();

        if (projectMemberRepository.existsByProjectAndWorkspaceMember(project, targetWorkspaceMember)) {
            throw new ProjectMemberAlreadyExistsException();
        }

        projectMemberRepository.save(targetProjectMember);
    }
}
