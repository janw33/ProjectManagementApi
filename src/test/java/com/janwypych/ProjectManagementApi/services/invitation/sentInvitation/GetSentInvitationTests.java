package com.janwypych.ProjectManagementApi.services.invitation.sentInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.sentInvitation.BaseTestSentInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.SentInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
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
public class GetSentInvitationTests extends BaseTestSentInvitation {
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
    public void shouldThrowWorkspaceNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                WorkspaceNotFoundException.class,
                () -> invitationService.getSentInvitation(user, workspace.getId(), invitation.getId())
        );
    }

    @Test
    public void shouldThrowInvitationNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(invitationRepository.findByIdAndWorkspace(invitation.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                InvitationNotFoundException.class,
                () -> invitationService.getSentInvitation(user, workspace.getId(), invitation.getId())
        );
    }

    @Test
    public void shouldGetInvitationWhenInvitationExists() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(invitationRepository.findByIdAndWorkspace(invitation.getId(), workspace))
                .thenReturn(Optional.of(invitation));

        SentInvitationDetailsResponse result = invitationService.getSentInvitation(user, workspace.getId(), invitation.getId());

        assertEquals(invitation.getId(), result.getId());
        assertEquals(user.getUsername(), result.getSenderUsername());
        assertEquals(user.getEmail(), result.getSenderEmail());
        assertEquals(workspaceMember.getRole(), result.getSenderRole());
        assertEquals(user2.getUsername(), result.getReceiverUsername());
        assertEquals(user2.getEmail(), result.getReceiverEmail());
        assertEquals(invitation.getStatus(), result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getExpiresAt());
    }
}
