package com.janwypych.ProjectManagementApi.services.invitation.sentInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.sentInvitation.BaseTestSentInvitation;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationAlreadyProcessedException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteSentInvitationTests extends BaseTestSentInvitation {
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
                () -> invitationService.deleteSentInvitation(user, workspace.getId(), invitation.getId())
        );

        verify(invitationRepository, never()).delete(any(Invitation.class));
    }

    @Test
    public void shouldThrowInvitationNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(invitationRepository.findByIdAndWorkspace(invitation.getId(), workspace))
                .thenReturn(Optional.empty());

        assertThrows(
                InvitationNotFoundException.class,
                () -> invitationService.deleteSentInvitation(user, workspace.getId(), invitation.getId())
        );

        verify(invitationRepository, never()).delete(any(Invitation.class));
    }

    @Test
    public void shouldThrowInvitationAlreadyProcessedExceptionWhenStatusIsNotPending() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(invitationRepository.findByIdAndWorkspace(invitation.getId(), workspace))
                .thenReturn(Optional.of(invitation));

        invitation.setStatus(InvitationStatus.EXPIRED);

        assertThrows(
                InvitationAlreadyProcessedException.class,
                () -> invitationService.deleteSentInvitation(user, workspace.getId(), invitation.getId())
        );

        verify(invitationRepository, never()).delete(any(Invitation.class));
    }

    @Test
    public void shouldDeleteInvitation() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(invitationRepository.findByIdAndWorkspace(invitation.getId(), workspace))
                .thenReturn(Optional.of(invitation));

        invitation.setStatus(InvitationStatus.PENDING);

        invitationService.deleteSentInvitation(user, workspace.getId(), invitation.getId());

        verify(invitationRepository).delete(invitation);
    }
}
