package com.janwypych.ProjectManagementApi.services.auth;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.auth.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.auth.RegisterRequest;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.exceptions.auth.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.UsernameAlreadyExistsException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void shouldThrowUsernameAlreadyExistsException() {
        RegisterRequest request = TestDataUtil.createUserRequest();

        when(userRepository.existsByUsername(request.getUsername()))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {
        RegisterRequest request = TestDataUtil.createUserRequest();

        when(userRepository.existsByUsername(request.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = TestDataUtil.createUserRequest();

        when(userRepository.existsByUsername(request.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id(1L)
                .username(request.getUsername())
                .email(request.getEmail())
                .password("hashedPassword")
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(savedUser))
                .thenReturn("jwt-token");

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(savedUser);
    }
}