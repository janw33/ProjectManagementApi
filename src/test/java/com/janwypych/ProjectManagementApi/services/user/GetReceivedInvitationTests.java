package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetReceivedInvitationTests extends BaseTestUser {
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
    public void shouldThrowInvitationNotFoundException() {
        when(invitationRepository.findByIdAndReceiverUser(invitation.getId(), receiverUser))
                .thenReturn(Optional.empty());

        assertThrows(
                InvitationNotFoundException.class,
                () -> userService.getReceivedInvitation(receiverUser, invitation.getId())
        );
    }

    @Test
    public void shouldGetReceivedInvitation() {
        when(invitationRepository.findByIdAndReceiverUser(invitation.getId(), receiverUser))
                .thenReturn(Optional.of(invitation));

        ReceivedInvitationDetailsResponse result = userService.getReceivedInvitation(receiverUser, invitation.getId());

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
