package com.janwypych.ProjectManagementApi.exceptions.invitation;

public class PendingInvitationAlreadyExistsException extends RuntimeException{
    public PendingInvitationAlreadyExistsException() {super("Pending invitation already exists");}
}
