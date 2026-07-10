package com.janwypych.ProjectManagementApi.exceptions.workspace;

public class WorkspaceNotFoundException extends RuntimeException{
    public WorkspaceNotFoundException(String message) {super(message);}
    public WorkspaceNotFoundException() {super("Workspace not found");}
}
