package com.janwypych.ProjectManagementApi.services.invitation.sentInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.BaseTestSentInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.SentInvitationSummaryResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetSentInvitationsTests extends BaseTestSentInvitation {
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
                () -> invitationService.getSentInvitations(user, workspace.getId(), Pageable.unpaged())
        );
    }

    @Test
    public void shouldGetInvitationsWhenWorkspaceIsFound() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUser(workspace.getId(), user))
                .thenReturn(Optional.of(workspaceMember));

        when(invitationRepository.findAllByWorkspace(workspace, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(invitation)));

        Page<SentInvitationSummaryResponse> sentInvitations = invitationService.getSentInvitations(user, workspace.getId(), Pageable.unpaged());

        assertEquals(1, sentInvitations.getTotalElements());
        assertEquals(invitation.getId(), sentInvitations.getContent().getFirst().getId());
        assertEquals(invitation.getSenderWorkspaceMember().getUser().getUsername(), sentInvitations.getContent().getFirst().getSenderUsername());
        assertEquals(invitation.getSenderWorkspaceMember().getRole(), sentInvitations.getContent().getFirst().getSenderRole());
        assertEquals(invitation.getReceiverUser().getUsername(), sentInvitations.getContent().getFirst().getReceiverUsername());
        assertEquals(invitation.getStatus(), sentInvitations.getContent().getFirst().getStatus());
        assertNotNull(sentInvitations.getContent().getFirst().getExpiresAt());
    }
}
