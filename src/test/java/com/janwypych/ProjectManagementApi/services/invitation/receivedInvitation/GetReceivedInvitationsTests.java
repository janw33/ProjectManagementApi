package com.janwypych.ProjectManagementApi.services.invitation.receivedInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.receivedInvitation.BaseTestReceivedInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.receivedInvitation.ReceivedInvitationSummaryResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetReceivedInvitationsTests extends BaseTestReceivedInvitation {
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
    public void shouldGetReceivedInvitations() {
        when(invitationRepository.findAllByReceiverUser(receiverUser, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(invitation)));

        Page<ReceivedInvitationSummaryResponse> result = invitationService.getReceivedInvitations(receiverUser, Pageable.unpaged());

        ReceivedInvitationSummaryResponse first = result.getContent().getFirst();

        assertEquals(1, result.getTotalElements());
        assertEquals(invitation.getId(), first.getId());
        assertEquals(invitation.getStatus(), first.getStatus());
        assertEquals(workspace.getName(), first.getWorkspaceName());
        assertEquals(senderWorkspaceMember.getUser().getUsername(), first.getSenderUsername());
        assertEquals(senderWorkspaceMember.getRole(), first.getSenderRole());
        assertNotNull(first.getExpiresAt());
    }
}
