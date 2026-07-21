package com.janwypych.ProjectManagementApi.BaseTest.user;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.user.UpdateCurrentUserRequest;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public abstract  class BaseTestUser {
    protected User receiverUser;
    protected User senderUser;
    protected Workspace workspace;
    protected WorkspaceMember senderWorkspaceMember;
    protected Invitation invitation;

    protected UpdateCurrentUserRequest updateCurrentUserRequest;

    @BeforeEach
    void setupBase() {
        receiverUser = TestDataUtil.user();
        senderUser = TestDataUtil.user2();
        workspace = TestDataUtil.workspace();
        senderWorkspaceMember = TestDataUtil.workspaceMember(senderUser, workspace);
        invitation = TestDataUtil.invitationPending(senderWorkspaceMember, workspace, receiverUser);
        updateCurrentUserRequest = TestDataUtil.updateCurrentUserRequest();
    }
}
