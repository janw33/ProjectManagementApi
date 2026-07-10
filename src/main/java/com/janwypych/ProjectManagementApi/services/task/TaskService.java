package com.janwypych.ProjectManagementApi.services.task;

import com.janwypych.ProjectManagementApi.dtos.task.*;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.task.TaskNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.task.TaskMapper;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.task.TaskRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    public TaskService(WorkspaceMemberRepository workspaceMemberRepository, ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository, TaskMapper taskMapper, TaskRepository taskRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
    }


    public TaskIdResponse createTask(User currentUser, CreateTaskRequest request, Long workspaceId, Long projectId) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        ProjectMember assignee = projectMemberRepository.findByIdAndProject(request.getAssigneeProjectMemberId(), project)
                .orElseThrow(ProjectMemberNotFoundException::new);

        Task task = taskMapper.toEntity(request, project, assignee);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toIdResponse(savedTask);
    }

    public Page<TaskSummaryResponse> getTasks(User currentUser, Long workspaceId, Long projectId, Pageable pageable) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        Page<Task> tasks = taskRepository.findAllByProject(project, pageable);

        return tasks.map(taskMapper::toSummaryResponse);
    }

    public TaskDetailsResponse getTask(User currentUser, Long workspaceId, Long projectId, Long taskId) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        Task task = taskRepository.findByIdAndProject(taskId, project)
                .orElseThrow(TaskNotFoundException::new);

        return taskMapper.toDetailsResponse(task);
    }

    @Transactional
    public TaskIdResponse updateTask(User currentUser, UpdateTaskRequest updateTaskRequest, Long workspaceId, Long projectId, Long taskId) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        Task task = taskRepository.findByIdAndProject(taskId, project)
                .orElseThrow(TaskNotFoundException::new);

        if (updateTaskRequest.getName() != null) {
            task.setName(updateTaskRequest.getName());
        }

        if (updateTaskRequest.getDescription() != null) {
            task.setDescription(updateTaskRequest.getDescription());
        }

        if (updateTaskRequest.getStatus() != null) {
            task.setStatus(updateTaskRequest.getStatus());
        }

        if (updateTaskRequest.getAssigneeProjectMemberId() != null) {
            ProjectMember assignee = projectMemberRepository
                    .findByIdAndProject(updateTaskRequest.getAssigneeProjectMemberId(), project)
                    .orElseThrow(ProjectMemberNotFoundException::new);

            task.setAssignee(assignee);
        }

        return taskMapper.toIdResponse(task);
    }
}
