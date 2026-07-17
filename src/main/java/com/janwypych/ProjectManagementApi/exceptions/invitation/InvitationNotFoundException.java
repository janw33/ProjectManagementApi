package com.janwypych.ProjectManagementApi.exceptions.invitation;

public class InvitationNotFoundException extends RuntimeException{
    public InvitationNotFoundException() {super("Invitation not found");
    }
}
