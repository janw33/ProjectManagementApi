package com.janwypych.ProjectManagementApi.exceptions.invitation;

public class InvitationExpiredException extends RuntimeException{
    public InvitationExpiredException() {super("Invitation has expired");
    }
}
