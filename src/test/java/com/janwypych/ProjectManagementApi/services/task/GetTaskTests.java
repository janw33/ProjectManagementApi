package com.janwypych.ProjectManagementApi.services.task;

import com.janwypych.ProjectManagementApi.BaseTestTask;
import com.janwypych.ProjectManagementApi.dtos.task.TaskDetailsResponse;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.task.TaskNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.task.TaskMapper;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.task.TaskRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class GetTaskTests extends BaseTestTask {
    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private final TaskMapper taskMapper = new TaskMapper();

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(
                workspaceMemberRepository,
                projectRepository,
                projectMemberRepository,
                taskMapper,
                taskRepository
        );
    }

    @Test
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> taskService.getTask(user, workspace.getId(), project.getId(), task.getId())
        );

        verify(taskRepository, never()).findByIdAndProject(any(), any());
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> taskService.getTask(user, workspace.getId(), project.getId(), task.getId())
        );

        verify(taskRepository, never()).findByIdAndProject(any(), any());
    }

    @Test
    public void shouldThrowTaskNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTask(user, workspace.getId(), project.getId(), task.getId())
        );
    }

    @Test
    public void shouldReturnTaskDetailsWhenTaskExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        TaskDetailsResponse result = taskService.getTask(user, workspace.getId(), project.getId(), task.getId());

        assertEquals(task.getId(), result.getId());
        assertEquals(task.getName(), result.getName());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getAssignee().getWorkspaceMember().getUser().getUsername(), result.getAssigneeUsername());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }
}
