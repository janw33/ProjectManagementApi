package com.janwypych.ProjectManagementApi.services.task;

import com.janwypych.ProjectManagementApi.BaseTestTask;
import com.janwypych.ProjectManagementApi.dtos.task.TaskIdResponse;
import com.janwypych.ProjectManagementApi.entities.enums.TaskStatus;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.task.TaskMapper;
import com.janwypych.ProjectManagementApi.repositories.project.ProjectRepository;
import com.janwypych.ProjectManagementApi.repositories.projectMember.ProjectMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.task.TaskRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateTaskTests extends BaseTestTask {
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
                () -> taskService.createTask(user, createTaskRequest, workspace.getId(), project.getId())
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void shouldThrowProjectNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> taskService.createTask(user, createTaskRequest, workspace.getId(), project.getId())
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void shouldThrowProjectMemberNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.findByIdAndProject(createTaskRequest.getAssigneeProjectMemberId(), project))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectMemberNotFoundException.class,
                () -> taskService.createTask(user, createTaskRequest, workspace.getId(), project.getId())
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void shouldCreateTaskWhenProjectMemberExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(projectMemberRepository.findByIdAndProject(createTaskRequest.getAssigneeProjectMemberId(), project))
                .thenReturn(Optional.of(projectMember));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        TaskIdResponse result = taskService.createTask(user, createTaskRequest, workspace.getId(), project.getId());

        assertEquals(task.getId(), result.getId());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(captor.capture());

        Task savedTask = captor.getValue();

        assertEquals(createTaskRequest.getName(), savedTask.getName());
        assertEquals(createTaskRequest.getDescription(), savedTask.getDescription());
        assertEquals(TaskStatus.NOT_STARTED, savedTask.getStatus());
        assertEquals(project, savedTask.getProject());
        assertEquals(projectMember, savedTask.getAssignee());
    }
}
