package com.janwypych.ProjectManagementApi.services.task;

import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.dtos.task.TaskIdResponse;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.task.TaskMapper;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.task.TaskRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    public TaskService(WorkspaceMemberRepository workspaceMemberRepository, ProjectRepository projectRepository, TaskMapper taskMapper, TaskRepository taskRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
    }

//    public TaskIdResponse createTask(User currentUser, CreateTaskRequest request, Long workspaceId, Long projectId) {
//        WorkspaceMember member = workspaceMemberRepository
//                .findByWorkspaceIdAndUser(workspaceId, currentUser)
//                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
//
//        Workspace workspace = member.getWorkspace();
//
//        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
//                .orElseThrow(ProjectNotFoundException::new);
//
//        Task task = taskMapper.toEntity(request);
//    }
}
