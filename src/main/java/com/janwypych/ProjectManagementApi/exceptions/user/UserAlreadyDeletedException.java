package com.janwypych.ProjectManagementApi.exceptions.user;

public class UserAlreadyDeletedException extends RuntimeException{
    public UserAlreadyDeletedException() {super("User has already been deleted");
    }
}
