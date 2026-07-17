package com.janwypych.ProjectManagementApi.exceptions.user;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {super("User not found");}
}
