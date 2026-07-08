package com.janwypych.ProjectManagementApi.exceptions.auth;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message) {super(message);}
}
