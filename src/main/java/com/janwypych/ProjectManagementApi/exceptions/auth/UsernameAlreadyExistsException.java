package com.janwypych.ProjectManagementApi.exceptions.auth;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String message) {super(message);}
}
