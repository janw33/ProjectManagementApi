package com.janwypych.ProjectManagementApi.exceptions.task;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException() {super("Task not found");}
}
