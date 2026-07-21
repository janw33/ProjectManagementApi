package com.janwypych.ProjectManagementApi.BaseTest.invitation.receivedInvitation;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public class BaseTestReceivedInvitation {
    protected User receiverUser;
    protected User senderUser;
    protected Workspace workspace;
    protected WorkspaceMember senderWorkspaceMember;
    protected WorkspaceMember receiverWorkspaceMember;
    protected Invitation invitation;

    @BeforeEach
    void setupBase() {
        receiverUser = TestDataUtil.user();
        senderUser = TestDataUtil.user2();
        workspace = TestDataUtil.workspace();
        senderWorkspaceMember = TestDataUtil.workspaceMember(senderUser, workspace);
        receiverWorkspaceMember = TestDataUtil.workspaceMember2(receiverUser, workspace);
        invitation = TestDataUtil.invitationPending(senderWorkspaceMember, workspace, receiverUser);
    }
}
