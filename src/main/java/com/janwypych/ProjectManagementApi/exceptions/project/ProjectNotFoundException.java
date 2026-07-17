package com.janwypych.ProjectManagementApi.exceptions.project;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException() {super("Project not found");}
}
