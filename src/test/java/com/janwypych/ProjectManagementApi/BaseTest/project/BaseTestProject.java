package com.janwypych.ProjectManagementApi.BaseTest.project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public abstract  class BaseTestProject {
    protected User user;
    protected Workspace workspace;
    protected WorkspaceMember workspaceMember;
    protected Project project;

    @BeforeEach
    void setupBase() {
        user = TestDataUtil.user();
        workspace = TestDataUtil.workspace();
        workspaceMember = TestDataUtil.workspaceMember(user, workspace);
        project = TestDataUtil.project(workspace);
    }
}
