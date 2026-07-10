package com.janwypych.ProjectManagementApi.exceptions.projectMember;

public class ProjectMemberNotFoundException extends RuntimeException{
    public ProjectMemberNotFoundException() {super("Project member not found");
    }
}
