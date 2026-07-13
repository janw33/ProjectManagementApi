package com.janwypych.ProjectManagementApi.services.comment;

import com.janwypych.ProjectManagementApi.BaseTestComment;
import com.janwypych.ProjectManagementApi.dtos.comment.CommentResponse;
import com.janwypych.ProjectManagementApi.entities.comment.Comment;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class GetCommentsTests extends BaseTestComment {
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
                () -> commentService.getComments(user, workspace.getId(), project.getId(), task.getId(), Pageable.unpaged())
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
                () -> commentService.getComments(user, workspace.getId(), project.getId(), task.getId(), Pageable.unpaged())
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
                () -> commentService.getComments(user, workspace.getId(), project.getId(), task.getId(), Pageable.unpaged())
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
                () -> commentService.getComments(user, workspace.getId(), project.getId(), task.getId(), Pageable.unpaged())
        );
    }

    @Test
    public void shouldGetCommentsWhenTaskExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project))
                .thenReturn(true);

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(commentRepository.findAllByTask(task, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(comment)));

        Page<CommentResponse> comments = commentService.getComments(user, workspace.getId(), project.getId(), task.getId(), Pageable.unpaged());

        assertEquals(1, comments.getTotalElements());
        assertEquals(comment.getId(), comments.getContent().getFirst().getId());
        assertEquals(comment.getContent(), comments.getContent().getFirst().getContent());
        assertEquals(projectMember.getWorkspaceMember().getUser().getUsername(), comments.getContent().getFirst().getAuthorUsername());
        assertNotNull(comments.getContent().getFirst().getCreatedAt());
        assertFalse(comments.getContent().getFirst().isEdited());    }

    @Test
    public void shouldGetEditedCommentWhenUpdatedAtIsAfterCreatedAt() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.existsByWorkspaceMemberAndProject(workspaceMember, project))
                .thenReturn(true);

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        comment.setUpdatedAt(LocalDateTime.now().plusHours(1));

        when(commentRepository.findAllByTask(task, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(comment)));

        Page<CommentResponse> comments = commentService.getComments(user, workspace.getId(), project.getId(), task.getId(), Pageable.unpaged());

        assertEquals(1, comments.getTotalElements());
        assertEquals(comment.getId(), comments.getContent().getFirst().getId());
        assertEquals(comment.getContent(), comments.getContent().getFirst().getContent());
        assertEquals(projectMember.getWorkspaceMember().getUser().getUsername(), comments.getContent().getFirst().getAuthorUsername());
        assertNotNull(comments.getContent().getFirst().getCreatedAt());
        assertTrue(comments.getContent().getFirst().isEdited());
    }
}
