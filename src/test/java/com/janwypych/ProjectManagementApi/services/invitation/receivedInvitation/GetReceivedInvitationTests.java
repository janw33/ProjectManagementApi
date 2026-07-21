package com.janwypych.ProjectManagementApi.services.invitation.receivedInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.receivedInvitation.BaseTestReceivedInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.receivedInvitation.ReceivedInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetReceivedInvitationTests extends BaseTestReceivedInvitation {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private InvitationRepository invitationRepository;

    private final InvitationMapper invitationMapper = new InvitationMapper();

    private InvitationService invitationService;

    @BeforeEach
    void setUp() {
        invitationService = new InvitationService(
                userRepository,
                workspaceMemberRepository,
                invitationRepository,
                invitationMapper
        );
    }

    @Test
    public void shouldThrowInvitationNotFoundException() {
        when(invitationRepository.findByIdAndReceiverUser(invitation.getId(), receiverUser))
                .thenReturn(Optional.empty());

        assertThrows(
                InvitationNotFoundException.class,
                () -> invitationService.getReceivedInvitation(receiverUser, invitation.getId())
        );
    }

    @Test
    public void shouldGetReceivedInvitation() {
        when(invitationRepository.findByIdAndReceiverUser(invitation.getId(), receiverUser))
                .thenReturn(Optional.of(invitation));

        ReceivedInvitationDetailsResponse result = invitationService.getReceivedInvitation(receiverUser, invitation.getId());

        assertEquals(invitation.getId(), result.getId());
        assertEquals(invitation.getStatus(), result.getStatus());
        assertEquals(workspace.getName(), result.getWorkspaceName());
        assertEquals(workspace.getDescription(), result.getWorkspaceDescription());
        assertEquals(senderUser.getUsername(), result.getSenderUsername());
        assertEquals(senderUser.getEmail(), result.getSenderEmail());
        assertEquals(senderWorkspaceMember.getRole(), result.getSenderRole());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getExpiresAt());
    }
}
