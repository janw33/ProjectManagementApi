package com.janwypych.ProjectManagementApi.exceptions.workspaceMember;

public class WorkspaceMemberAlreadyDeletedException extends RuntimeException{
    public WorkspaceMemberAlreadyDeletedException() {super("Workspace member has already been deleted");
    }
}
