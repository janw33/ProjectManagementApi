package com.janwypych.ProjectManagementApi.services.comment;

import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.entities.comment.Comment;
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
import com.janwypych.ProjectManagementApi.mappers.comment.CommentMapper;
import com.janwypych.ProjectManagementApi.repositories.comment.CommentRepository;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.task.TaskRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public CommentService(WorkspaceMemberRepository workspaceMemberRepository, ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository, TaskRepository taskRepository, CommentMapper commentMapper, CommentRepository commentRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }

    public void createComment(User currentUser, CreateCommentRequest request, Long workspaceId, Long projectId, Long taskId) {
        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = workspaceMember.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        ProjectMember author = projectMemberRepository
                .findByWorkspaceMemberAndProject(workspaceMember, project)
                .orElseThrow(ProjectMemberNotFoundException::new);

        Task task = taskRepository.findByIdAndProject(taskId, project)
                .orElseThrow(TaskNotFoundException::new);

        Comment comment = commentMapper.toEntity(request, author, task);
        Comment savedComment = commentRepository.save(comment);
    }
}
