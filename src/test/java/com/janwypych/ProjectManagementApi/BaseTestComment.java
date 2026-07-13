package com.janwypych.ProjectManagementApi;

import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.entities.comment.Comment;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestComment {
    protected User user;
    protected Workspace workspace;
    protected WorkspaceMember workspaceMember;
    protected Project project;
    protected ProjectMember projectMember;
    protected Task task;
    protected Comment comment;

    protected CreateCommentRequest createCommentRequest;

    @BeforeEach
    void setupBase() {
        user = TestDataUtil.user();
        workspace = TestDataUtil.workspace();
        workspaceMember = TestDataUtil.workspaceMember(user, workspace);
        project = TestDataUtil.project();
        projectMember = TestDataUtil.projectMember(workspaceMember, project);
        task = TestDataUtil.task(projectMember, project);
        comment = TestDataUtil.comment(projectMember, task);

        createCommentRequest = TestDataUtil.createCommentRequest();
    }

}
