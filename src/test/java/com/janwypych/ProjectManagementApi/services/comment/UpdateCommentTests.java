package com.janwypych.ProjectManagementApi.services.comment;

import com.janwypych.ProjectManagementApi.BaseTest.comment.BaseTestComment;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateCommentTests extends BaseTestComment {
    private final CommentMapper commentMapper = new CommentMapper();

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(
                workspaceMemberRepository,
                projectRepository,
                projectMemberRepository,
                taskRepository,
                commentMapper,
                commentRepository
        );
    }

    @Test
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> commentService.updateComment(user, updateCommentRequest ,workspace.getId(), project.getId(), task.getId(), comment.getId())
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
                () -> commentService.updateComment(user, updateCommentRequest ,workspace.getId(), project.getId(), task.getId(), comment.getId())
        );
    }

    @Test
    public void shouldThrowProjectMemberNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project))
                .thenReturn(false);

        assertThrows(
                ProjectMemberNotFoundException.class,
                () -> commentService.updateComment(user, updateCommentRequest ,workspace.getId(), project.getId(), task.getId(), comment.getId())
        );
    }

    @Test
    public void shouldThrowTaskNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project))
                .thenReturn(true);

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> commentService.updateComment(user, updateCommentRequest ,workspace.getId(), project.getId(), task.getId(), comment.getId())
        );
    }

    @Test
    public void shouldThrowCommentNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project))
                .thenReturn(true);

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(commentRepository.findByIdAndTask(comment.getId(), task))
                .thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.updateComment(user, updateCommentRequest ,workspace.getId(), project.getId(), task.getId(), comment.getId())
        );
    }

    @Test
    public void shouldUpdateCommentWhenCommentIsFound() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project))
                .thenReturn(true);

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(commentRepository.findByIdAndTask(comment.getId(), task))
                .thenReturn(Optional.of(comment));

        assertNotEquals(updateCommentRequest.getContent(), comment.getContent());

        commentService.updateComment(user, updateCommentRequest ,workspace.getId(), project.getId(), task.getId(), comment.getId());

        assertEquals(updateCommentRequest.getContent(), comment.getContent());
    }

    @Test
    public void shouldNotUpdateCommentWhenContentIsNull() {
        updateCommentRequest.setContent(null);

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project))
                .thenReturn(true);

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(commentRepository.findByIdAndTask(comment.getId(), task))
                .thenReturn(Optional.of(comment));

        String originalContent = comment.getContent();

        commentService.updateComment(user, updateCommentRequest ,workspace.getId(), project.getId(), task.getId(), comment.getId());

        assertEquals(originalContent, comment.getContent());
    }
}
