package com.janwypych.ProjectManagementApi.services.comment;

import com.janwypych.ProjectManagementApi.dtos.comment.CommentResponse;
import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.dtos.comment.UpdateCommentRequest;
import com.janwypych.ProjectManagementApi.entities.comment.Comment;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.comment.CommentNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.task.TaskNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.comment.CommentMapper;
import com.janwypych.ProjectManagementApi.repositories.comment.CommentRepository;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.task.TaskRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<CommentResponse> getComments(User currentUser, Long workspaceId, Long projectId, Long taskId, Pageable pageable) {
        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = workspaceMember.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        if (!projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project)) {
            throw new ProjectMemberNotFoundException();
        }

        Task task = taskRepository.findByIdAndProject(taskId, project)
                .orElseThrow(TaskNotFoundException::new);

        Page<Comment> comments = commentRepository.findAllByTaskOrderByCreatedAtAsc(task, pageable);

        return comments.map(commentMapper::toResponse);
    }

    @Transactional
    public void updateComment(User currentUser, UpdateCommentRequest request, Long workspaceId, Long projectId, Long taskId, Long commentId) {
        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = workspaceMember.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        if (!projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project)) {
            throw new ProjectMemberNotFoundException();
        }

        Task task = taskRepository.findByIdAndProject(taskId, project)
                .orElseThrow(TaskNotFoundException::new);

        Comment comment = commentRepository.findByIdAndTask(commentId, task)
                .orElseThrow(CommentNotFoundException::new);

        if(request.getContent() != null) {
            comment.setContent(request.getContent());
        }
    }

    public void deleteComment(User currentUser, Long workspaceId, Long projectId, Long taskId, Long commentId) {
        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = workspaceMember.getWorkspace();

        Project project = projectRepository.findByIdAndWorkspace(projectId, workspace)
                .orElseThrow(ProjectNotFoundException::new);

        Task task = taskRepository.findByIdAndProject(taskId, project)
                .orElseThrow(TaskNotFoundException::new);

        Comment comment = commentRepository.findByIdAndTask(commentId, task)
                .orElseThrow(CommentNotFoundException::new);

        commentRepository.delete(comment);
    }
}
