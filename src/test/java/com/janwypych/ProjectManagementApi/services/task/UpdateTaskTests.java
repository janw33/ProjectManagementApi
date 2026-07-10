package com.janwypych.ProjectManagementApi.services.task;

import com.janwypych.ProjectManagementApi.BaseTestTask;
import com.janwypych.ProjectManagementApi.dtos.task.TaskIdResponse;
import com.janwypych.ProjectManagementApi.entities.enums.TaskStatus;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateTaskTests extends BaseTestTask {
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
                () -> taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId())
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
                () -> taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId())
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
                () -> taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId())
        );
    }

    @Test
    public void shouldFullUpdateTaskWhenTaskExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(projectMemberRepository.findByIdAndProject(updateTaskRequest.getAssigneeProjectMemberId(), project))
                .thenReturn(Optional.of(projectMember2));

        assertNotEquals(updateTaskRequest.getName(), task.getName());
        assertNotEquals(updateTaskRequest.getDescription(), task.getDescription());
        assertNotEquals(updateTaskRequest.getStatus(), task.getStatus());
        assertNotEquals(updateTaskRequest.getAssigneeProjectMemberId(), task.getAssignee().getId());

        TaskIdResponse result = taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId());

        assertEquals(task.getId(), result.getId());
        assertEquals(updateTaskRequest.getName(), task.getName());
        assertEquals(updateTaskRequest.getDescription(), task.getDescription());
        assertEquals(updateTaskRequest.getStatus(), task.getStatus());
        assertEquals(updateTaskRequest.getAssigneeProjectMemberId(), task.getAssignee().getId());
    }

    public void shouldThrowProjectMemberNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(projectMemberRepository.findByIdAndProject(updateTaskRequest.getAssigneeProjectMemberId(), project))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectMemberNotFoundException.class,
                () -> taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId())
        );
    }

    @Test
    public void shouldOnlyUpdateNameWhenEverythingElseIsNull() {
        String originalDescription = task.getDescription();
        TaskStatus originalStatus = task.getStatus();
        Long originalAssigneeProjectMemberId = task.getAssignee().getId();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        updateTaskRequest.setDescription(null);
        updateTaskRequest.setStatus(null);
        updateTaskRequest.setAssigneeProjectMemberId(null);

        assertNotEquals(updateTaskRequest.getName(), task.getName());

        TaskIdResponse result = taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId());

        assertEquals(task.getId(), result.getId());
        assertEquals(updateTaskRequest.getName(), task.getName());
        assertEquals(originalDescription, task.getDescription());
        assertEquals(originalStatus, task.getStatus());
        assertEquals(originalAssigneeProjectMemberId, task.getAssignee().getId());
    }

    @Test
    public void shouldOnlyUpdateAssigneeWhenEverythingElseIsNull() {
        String originalName = task.getName();
        String originalDescription = task.getDescription();
        TaskStatus originalStatus = task.getStatus();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(projectMemberRepository.findByIdAndProject(updateTaskRequest.getAssigneeProjectMemberId(), project))
                .thenReturn(Optional.of(projectMember2));

        updateTaskRequest.setName(null);
        updateTaskRequest.setStatus(null);
        updateTaskRequest.setDescription(null);

        assertNotEquals(updateTaskRequest.getAssigneeProjectMemberId(), task.getAssignee().getId());

        TaskIdResponse result = taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId());

        assertEquals(task.getId(), result.getId());
        assertEquals(originalName, task.getName());
        assertEquals(originalDescription, task.getDescription());
        assertEquals(originalStatus, task.getStatus());
        assertEquals(updateTaskRequest.getAssigneeProjectMemberId(), task.getAssignee().getId());
    }

    @Test
    public void shouldOnlyUpdateNameAndAssigneeWhenEverythingElseIsNull() {
        String originalDescription = task.getDescription();
        TaskStatus originalStatus = task.getStatus();

        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(projectRepository.findByIdAndWorkspace(project.getId(), workspace))
                .thenReturn(Optional.of(project));

        when(taskRepository.findByIdAndProject(task.getId(), project))
                .thenReturn(Optional.of(task));

        when(projectMemberRepository.findByIdAndProject(updateTaskRequest.getAssigneeProjectMemberId(), project))
                .thenReturn(Optional.of(projectMember2));

        updateTaskRequest.setStatus(null);
        updateTaskRequest.setDescription(null);

        assertNotEquals(updateTaskRequest.getName(), task.getName());
        assertNotEquals(updateTaskRequest.getAssigneeProjectMemberId(), task.getAssignee().getId());

        TaskIdResponse result = taskService.updateTask(user, updateTaskRequest, workspace.getId(), project.getId(), task.getId());

        assertEquals(task.getId(), result.getId());
        assertEquals(updateTaskRequest.getName(), task.getName());
        assertEquals(originalDescription, task.getDescription());
        assertEquals(originalStatus, task.getStatus());
        assertEquals(updateTaskRequest.getAssigneeProjectMemberId(), task.getAssignee().getId());
    }
}