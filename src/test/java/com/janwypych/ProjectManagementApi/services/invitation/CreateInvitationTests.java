package com.janwypych.ProjectManagementApi.services.invitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.BaseTestInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.exceptions.invitation.PendingInvitationAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.user.UserNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.UserAlreadyWorkspaceMemberException;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateInvitationTests extends BaseTestInvitation {
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
                () -> invitationService.createInvitation(user, createInvitationRequest, workspace.getId())
        );

        verify(invitationRepository, never()).save(any(Invitation.class));
    }

    @Test
    public void shouldThrowUserNotFoundException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(userRepository.findById(createInvitationRequest.getReceiverUserId()))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> invitationService.createInvitation(user, createInvitationRequest, workspace.getId())
        );

        verify(invitationRepository, never()).save(any(Invitation.class));
    }

    @Test
    public void shouldThrowUserAlreadyWorkspaceMemberException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(userRepository.findById(createInvitationRequest.getReceiverUserId()))
                .thenReturn(Optional.of(user2));

        when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user2))
                .thenReturn(true);

        assertThrows(
                UserAlreadyWorkspaceMemberException.class,
                () -> invitationService.createInvitation(user, createInvitationRequest, workspace.getId())
        );

        verify(invitationRepository, never()).save(any(Invitation.class));
    }

    @Test
    public void shouldThrowPendingInvitationAlreadyExistsException() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(userRepository.findById(createInvitationRequest.getReceiverUserId()))
                .thenReturn(Optional.of(user2));

        when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user2))
                .thenReturn(false);

        when(invitationRepository.findByWorkspaceAndReceiverUserAndStatus(workspace, user2, InvitationStatus.PENDING))
                .thenReturn(Optional.of(invitation)); // invitation is on default pending

        assertThrows(
                PendingInvitationAlreadyExistsException.class,
                () -> invitationService.createInvitation(user, createInvitationRequest, workspace.getId())
        );

        verify(invitationRepository, never()).save(any(Invitation.class));
    }

    @Test
    public void shouldCreateInvitation() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(userRepository.findById(createInvitationRequest.getReceiverUserId()))
                .thenReturn(Optional.of(user2));

        when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user2))
                .thenReturn(false);

        when(invitationRepository.findByWorkspaceAndReceiverUserAndStatus(workspace, user2, InvitationStatus.PENDING))
                .thenReturn(Optional.empty());

        when(invitationRepository.save(any(Invitation.class)))
                .thenReturn(invitation);

        InvitationIdResponse result = invitationService.createInvitation(user, createInvitationRequest, workspace.getId());

        assertEquals(invitation.getId(), result.getId());

        ArgumentCaptor<Invitation> captor = ArgumentCaptor.forClass(Invitation.class);

        verify(invitationRepository).save(captor.capture());

        Invitation savedInvitation = captor.getValue();

        assertEquals(InvitationStatus.PENDING, savedInvitation.getStatus());
        assertEquals(workspace, savedInvitation.getWorkspace());
        assertEquals(workspaceMember, savedInvitation.getSenderWorkspaceMember());
        assertEquals(user2, savedInvitation.getReceiverUser());
        assertNotNull(savedInvitation.getExpiresAt());

    }
}
