package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
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
public class GetReceivedInvitationsTests extends BaseTestUser {
    private final UserMapper userMapper = new UserMapper();

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvitationRepository invitationRepository;

    private final InvitationMapper invitationMapper = new InvitationMapper();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                userMapper,
                invitationRepository,
                invitationMapper
        );
    }

    @Test
    public void shouldGetReceivedInvitations() {
        when(invitationRepository.findAllByReceiverUser(receiverUser, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(invitation)));

        Page<ReceivedInvitationSummaryResponse> result = userService.getReceivedInvitations(receiverUser, Pageable.unpaged());

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
