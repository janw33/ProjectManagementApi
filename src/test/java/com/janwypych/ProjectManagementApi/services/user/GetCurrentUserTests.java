package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.dtos.user.UserResponse;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class GetCurrentUserTests extends BaseTestUser {
    private final UserMapper userMapper = new UserMapper();

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private InvitationMapper invitationMapper;

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
    public void shouldGetCurrentUser() {
        UserResponse result = userService.getCurrentUser(receiverUser);

        assertEquals(receiverUser.getId(), result.getId());
        assertEquals(receiverUser.getUsername(), result.getUsername());
        assertEquals(receiverUser.getEmail(), result.getEmail());
        assertNotNull(result.getCreatedAt());
    }
}
