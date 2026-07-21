package com.janwypych.ProjectManagementApi.services.invitation.receivedInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.receivedInvitation.BaseTestReceivedInvitation;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationAlreadyProcessedException;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationExpiredException;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AcceptInvitationTests extends BaseTestReceivedInvitation {
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
                () -> invitationService.acceptInvitation(receiverUser, invitation.getId())
        );
    }

    @Test
    public void shouldThrowInvitationExpiredException() {
        when(invitationRepository.findByIdAndReceiverUser(invitation.getId(), receiverUser))
                .thenReturn(Optional.of(invitation));

        invitation.setExpiresAt(LocalDateTime.now().minusMinutes(2));

        assertThrows(
                InvitationExpiredException.class,
                () -> invitationService.acceptInvitation(receiverUser, invitation.getId())
        );
    }

    @Test
    public void shouldThrowInvitationAlreadyProcessedException() {
        when(invitationRepository.findByIdAndReceiverUser(invitation.getId(), receiverUser))
                .thenReturn(Optional.of(invitation));

        invitation.setStatus(InvitationStatus.ACCEPTED);

        assertThrows(
                InvitationAlreadyProcessedException.class,
                () -> invitationService.acceptInvitation(receiverUser, invitation.getId())
        );
    }

    @Test
    public void shouldAcceptInvitation() {
        when(invitationRepository.findByIdAndReceiverUser(invitation.getId(), receiverUser))
                .thenReturn(Optional.of(invitation));

        invitationService.acceptInvitation(receiverUser, invitation.getId());

        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());

        ArgumentCaptor<WorkspaceMember> captor = ArgumentCaptor.forClass(WorkspaceMember.class);

        verify(workspaceMemberRepository).save(captor.capture());

        WorkspaceMember savedMember = captor.getValue();

        assertEquals(WorkspaceRole.MEMBER, savedMember.getRole());
        assertEquals(workspace, savedMember.getWorkspace());
        assertEquals(receiverUser, savedMember.getUser());
    }
}
