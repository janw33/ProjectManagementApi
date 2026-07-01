package com.janwypych.ProjectManagementApi.services.auth;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.LoginRequest;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.exceptions.InvalidCredentialsException;
import com.janwypych.ProjectManagementApi.mappers.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.UserRepository;
import com.janwypych.ProjectManagementApi.security.JwtService;
import com.janwypych.ProjectManagementApi.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginTests {
    private final UserMapper userMapper = new UserMapper();

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userMapper,
                passwordEncoder,
                userRepository,
                jwtService
        );
    }

    @Test
    public void shouldThrowInvalidCredentialsExceptionWhenEmailIsInvalid() {
        LoginRequest request = TestDataUtil.loginRequest();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    public void shouldThrowInvalidCredentialsExceptionWhenPasswordIsInvalid() {
        LoginRequest request = TestDataUtil.loginRequest();
        User user = TestDataUtil.user();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    public void shouldLoginUserSuccessfully() {
        LoginRequest request = TestDataUtil.loginRequest();
        User user = TestDataUtil.user();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());

        verify(jwtService).generateToken(user);
        verify(passwordEncoder).matches(
                request.getPassword(),
                user.getPassword()
        );
    }
}
