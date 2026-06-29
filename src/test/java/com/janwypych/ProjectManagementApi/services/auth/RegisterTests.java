package com.janwypych.ProjectManagementApi.services.auth;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.CreateUserRequest;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.exceptions.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.UsernameAlreadyExistsException;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterTests {
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
    public void shouldThrowUsernameAlreadyExistsException() {
        CreateUserRequest request = TestDataUtil.createAccountRequest();

        when(userRepository.existsByUsername(anyString()))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    public void shouldThrowEmailAlreadyExistsException() {
        CreateUserRequest request = TestDataUtil.createAccountRequest();

        when(userRepository.existsByUsername(anyString()))
                .thenReturn(false);

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    public void shouldRegisterUserSuccessfully() {
        CreateUserRequest request = TestDataUtil.createAccountRequest();

        when(userRepository.existsByUsername(request.username()))
                .thenReturn(false);

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id(1L)
                .username(request.username())
                .email(request.email())
                .password("hashedPassword")
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(savedUser))
                .thenReturn("jwt-token");

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals("jwt-token", result.token());

        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(savedUser);
    }
}
