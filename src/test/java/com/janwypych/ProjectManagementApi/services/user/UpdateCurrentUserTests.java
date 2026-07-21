package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.auth.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateCurrentUserTests extends BaseTestUser {
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
    public void shouldThrowUsernameAlreadyExistsException() {
        when(userRepository.existsByUsername(updateCurrentUserRequest.getUsername()))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userService.updateCurrentUser(receiverUser, updateCurrentUserRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByUsername(updateCurrentUserRequest.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(updateCurrentUserRequest.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.updateCurrentUser(receiverUser, updateCurrentUserRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldUpdateCurrentUser() {
        when(userRepository.existsByUsername(updateCurrentUserRequest.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(updateCurrentUserRequest.getEmail()))
                .thenReturn(false);

        assertNotEquals(updateCurrentUserRequest.getUsername(), receiverUser.getUsername());
        assertNotEquals(updateCurrentUserRequest.getEmail(), receiverUser.getEmail());

        userService.updateCurrentUser(receiverUser, updateCurrentUserRequest);

        assertEquals(updateCurrentUserRequest.getUsername(), receiverUser.getUsername());
        assertEquals(updateCurrentUserRequest.getEmail(), receiverUser.getEmail());


        verify(userRepository).save(receiverUser);
    }

    @Test
    public void shouldUpdateOnlyNameWhenEmailIsNull() {
        String originalEmail = receiverUser.getEmail();
        updateCurrentUserRequest.setEmail(null);

        when(userRepository.existsByUsername(updateCurrentUserRequest.getUsername()))
                .thenReturn(false);

        assertNotEquals(updateCurrentUserRequest.getUsername(), receiverUser.getUsername());

        userService.updateCurrentUser(receiverUser, updateCurrentUserRequest);

        assertEquals(updateCurrentUserRequest.getUsername(), receiverUser.getUsername());
        assertEquals(originalEmail, receiverUser.getEmail());


        verify(userRepository).save(receiverUser);
    }

    @Test
    public void shouldUpdateOnlyEmailWhenUsernameIsNull() {
        String originalUsername = receiverUser.getUsername();
        updateCurrentUserRequest.setUsername(null);

        when(userRepository.existsByEmail(updateCurrentUserRequest.getEmail()))
                .thenReturn(false);

        assertNotEquals(updateCurrentUserRequest.getEmail(), receiverUser.getEmail());

        userService.updateCurrentUser(receiverUser, updateCurrentUserRequest);

        assertEquals(originalUsername, receiverUser.getUsername());
        assertEquals(updateCurrentUserRequest.getEmail(), receiverUser.getEmail());


        verify(userRepository).save(receiverUser);
    }

    @Test
    public void shouldNotModifyUserWhenRequestIsEmpty() {
        String originalUsername = receiverUser.getUsername();
        String originalEmail = receiverUser.getEmail();

        updateCurrentUserRequest.setUsername(null);
        updateCurrentUserRequest.setEmail(null);

        userService.updateCurrentUser(receiverUser, updateCurrentUserRequest);

        assertEquals(originalUsername, receiverUser.getUsername());
        assertEquals(originalEmail, receiverUser.getEmail());


        verify(userRepository).save(receiverUser);
    }
}
