package com.janwypych.ProjectManagementApi.exceptions.invitation;

public class InvitationAlreadyProcessedException extends RuntimeException{
    public InvitationAlreadyProcessedException() {super("Invitation has already been processed");}
}
