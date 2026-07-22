package com.janwypych.ProjectManagementApi.BaseTest.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestWorkspace {
    protected User user;
    protected Workspace workspace;
    protected WorkspaceMember workspaceMember;

    @BeforeEach
    void setupBase() {
        user = TestDataUtil.user();
        workspace = TestDataUtil.workspace();
        workspaceMember = TestDataUtil.workspaceMember(user, workspace);
    }
}
