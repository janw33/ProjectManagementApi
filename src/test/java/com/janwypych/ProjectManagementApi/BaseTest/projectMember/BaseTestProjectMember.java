package com.janwypych.ProjectManagementApi.BaseTest.projectMember;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.projectMember.CreateProjectMemberRequest;
import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.dtos.task.UpdateTaskRequest;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestProjectMember {
    protected User user;
    protected User user2;
    protected Workspace workspace;
    protected WorkspaceMember workspaceMember;
    protected WorkspaceMember workspaceMember2;
    protected Project project;
    protected ProjectMember projectMember;

    protected CreateProjectMemberRequest createProjectMemberRequest;
    @BeforeEach
    void setupBase() {
        user = TestDataUtil.user();
        user2 = TestDataUtil.user2();
        workspace = TestDataUtil.workspace();
        workspaceMember = TestDataUtil.workspaceMember(user, workspace);
        workspaceMember2 = TestDataUtil.workspaceMember2(user2, workspace);
        project = TestDataUtil.project(workspace);
        projectMember = TestDataUtil.projectMember(workspaceMember, project);

        createProjectMemberRequest = TestDataUtil.createProjectMemberRequest();
    }
}
