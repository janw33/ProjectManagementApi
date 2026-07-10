package com.janwypych.ProjectManagementApi.services.task;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestTask {
    protected User user;
    protected Workspace workspace;
    protected Project project;
    protected CreateTaskRequest createTaskRequest;
    protected ProjectMember projectMember;
    protected WorkspaceMember workspaceMember;
    protected Task task;

    @BeforeEach
    void setupBase() {
        user = TestDataUtil.user();
        workspace = TestDataUtil.workspace();
        workspaceMember = TestDataUtil.workspaceMember(user, workspace);
        project = TestDataUtil.project();
        createTaskRequest = TestDataUtil.createTaskRequest();
        projectMember = TestDataUtil.createProjectMember(workspaceMember, project);
        task = TestDataUtil.task(projectMember, project);
    }
}

