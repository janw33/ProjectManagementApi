package com.janwypych.ProjectManagementApi.exceptions.projectMember;

public class ProjectMemberAlreadyDeletedException extends RuntimeException{
    public ProjectMemberAlreadyDeletedException() {super("Project member has already been deleted");
    }
}
