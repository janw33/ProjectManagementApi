package com.janwypych.ProjectManagementApi.exceptions.workspaceMember;

public class WorkspaceMemberNotFoundException extends RuntimeException{
    public WorkspaceMemberNotFoundException() {super("Workspace member not found");}
}
