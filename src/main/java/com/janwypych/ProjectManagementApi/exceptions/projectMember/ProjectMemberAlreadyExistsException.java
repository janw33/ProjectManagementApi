package com.janwypych.ProjectManagementApi.exceptions.projectMember;

public class ProjectMemberAlreadyExistsException extends RuntimeException{
    public ProjectMemberAlreadyExistsException() {super("Project member already exists");}
}
