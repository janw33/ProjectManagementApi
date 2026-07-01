package com.janwypych.ProjectManagementApi.services;

import com.janwypych.ProjectManagementApi.dtos.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.LoginRequest;
import com.janwypych.ProjectManagementApi.dtos.RegisterRequest;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.exceptions.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.InvalidCredentialsException;
import com.janwypych.ProjectManagementApi.exceptions.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.mappers.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.UserRepository;
import com.janwypych.ProjectManagementApi.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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

    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new UsernameAlreadyExistsException("Username already exists");

        if(userRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, hashedPassword);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        return userMapper.toResponse(token);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException("Invalid email or password");

        String token = jwtService.generateToken(user);
        return userMapper.toResponse(token);
    }
}
