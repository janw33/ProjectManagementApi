package com.janwypych.ProjectManagementApi.BaseTest.invitation.sentInvitation;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.CreateInvitationRequest;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestSentInvitation {
    protected User user;
    protected User user2;
    protected Workspace workspace;
    protected WorkspaceMember workspaceMember;
    protected WorkspaceMember workspaceMember2;
    protected Invitation invitation;

    protected CreateInvitationRequest createInvitationRequest;
    @BeforeEach
    void setupBase() {
        user = TestDataUtil.user();
        user2 = TestDataUtil.user2();
        workspace = TestDataUtil.workspace();
        workspaceMember = TestDataUtil.workspaceMember(user, workspace);
        workspaceMember2 = TestDataUtil.workspaceMember2(user2, workspace);
        invitation = TestDataUtil.invitationPending(workspaceMember, workspace, user2);

        createInvitationRequest = TestDataUtil.createInvitationRequest();
    }
}
