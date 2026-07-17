package com.janwypych.ProjectManagementApi.services.auth;

import com.janwypych.ProjectManagementApi.dtos.auth.AuthResponse;
import com.janwypych.ProjectManagementApi.dtos.auth.LoginRequest;
import com.janwypych.ProjectManagementApi.dtos.auth.RegisterRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.auth.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.InvalidCredentialsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import com.janwypych.ProjectManagementApi.security.JwtService;
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
        return userMapper.toTokenResponse(token);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException("Invalid email or password");

        String token = jwtService.generateToken(user);
        return userMapper.toTokenResponse(token);
    }
}
