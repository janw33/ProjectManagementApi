package com.janwypych.ProjectManagementApi.services;

import com.janwypych.ProjectManagementApi.dtos.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.CreateUserRequest;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.exceptions.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.mappers.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.UserRepository;
import com.janwypych.ProjectManagementApi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse createUser(CreateUserRequest request) {
        if(userRepository.existsByUsername(request.username()))
            throw new UsernameAlreadyExistsException("Username already exists");

        if(userRepository.existsByEmail(request.email()))
            throw new EmailAlreadyExistsException("Email already exists");

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = userMapper.toEntity(request, hashedPassword);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        return userMapper.toResponse(token);
    }
}
