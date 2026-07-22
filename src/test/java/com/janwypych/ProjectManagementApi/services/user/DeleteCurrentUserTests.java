package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.user.UserAlreadyDeletedException;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteCurrentUserTests extends BaseTestUser {
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
    public void shouldThrowUserAlreadyDeletedException() {
        user.setActive(false);

        assertThrows(
                UserAlreadyDeletedException.class,
                () -> userService.deleteCurrentUser(user)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldDeleteCurrentUser() {
        userService.deleteCurrentUser(user);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }
}
