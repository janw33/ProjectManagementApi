package com.janwypych.ProjectManagementApi.exceptions.Project;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException() {super("Project not found");}
}
