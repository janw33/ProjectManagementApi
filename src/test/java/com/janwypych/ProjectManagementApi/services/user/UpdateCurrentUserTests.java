package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.auth.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
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

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                userMapper
        );
    }

    @Test
    public void shouldThrowUsernameAlreadyExistsException() {
        when(userRepository.existsByUsername(updateCurrentUserRequest.getUsername()))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userService.updateCurrentUser(user, updateCurrentUserRequest)
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
                () -> userService.updateCurrentUser(user, updateCurrentUserRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldUpdateCurrentUser() {
        when(userRepository.existsByUsername(updateCurrentUserRequest.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(updateCurrentUserRequest.getEmail()))
                .thenReturn(false);

        assertNotEquals(updateCurrentUserRequest.getUsername(), user.getUsername());
        assertNotEquals(updateCurrentUserRequest.getEmail(), user.getEmail());

        userService.updateCurrentUser(user, updateCurrentUserRequest);

        assertEquals(updateCurrentUserRequest.getUsername(), user.getUsername());
        assertEquals(updateCurrentUserRequest.getEmail(), user.getEmail());


        verify(userRepository).save(user);
    }

    @Test
    public void shouldUpdateOnlyNameWhenEmailIsNull() {
        String originalEmail = user.getEmail();
        updateCurrentUserRequest.setEmail(null);

        when(userRepository.existsByUsername(updateCurrentUserRequest.getUsername()))
                .thenReturn(false);

        assertNotEquals(updateCurrentUserRequest.getUsername(), user.getUsername());

        userService.updateCurrentUser(user, updateCurrentUserRequest);

        assertEquals(updateCurrentUserRequest.getUsername(), user.getUsername());
        assertEquals(originalEmail, user.getEmail());


        verify(userRepository).save(user);
    }

    @Test
    public void shouldUpdateOnlyEmailWhenUsernameIsNull() {
        String originalUsername = user.getUsername();
        updateCurrentUserRequest.setUsername(null);

        when(userRepository.existsByEmail(updateCurrentUserRequest.getEmail()))
                .thenReturn(false);

        assertNotEquals(updateCurrentUserRequest.getEmail(), user.getEmail());

        userService.updateCurrentUser(user, updateCurrentUserRequest);

        assertEquals(originalUsername, user.getUsername());
        assertEquals(updateCurrentUserRequest.getEmail(), user.getEmail());


        verify(userRepository).save(user);
    }

    @Test
    public void shouldNotModifyUserWhenRequestIsEmpty() {
        String originalUsername = user.getUsername();
        String originalEmail = user.getEmail();

        updateCurrentUserRequest.setUsername(null);
        updateCurrentUserRequest.setEmail(null);

        userService.updateCurrentUser(user, updateCurrentUserRequest);

        assertEquals(originalUsername, user.getUsername());
        assertEquals(originalEmail, user.getEmail());


        verify(userRepository).save(user);
    }
}
