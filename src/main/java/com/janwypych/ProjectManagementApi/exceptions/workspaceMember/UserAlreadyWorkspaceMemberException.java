package com.janwypych.ProjectManagementApi.exceptions.workspaceMember;

public class UserAlreadyWorkspaceMemberException extends RuntimeException{
    public UserAlreadyWorkspaceMemberException() {super("User is already in workspace");}
}
