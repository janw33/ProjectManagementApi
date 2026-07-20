package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.dtos.user.UpdateCurrentUserRequest;
import com.janwypych.ProjectManagementApi.dtos.user.UserResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.auth.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse getCurrentUser(User currentUser) {
        return userMapper.toResponse(currentUser);
    }

    @Transactional
    public void updateCurrentUser(User currentUser, UpdateCurrentUserRequest request) {
        if(request.getUsername() != null &&  !request.getUsername().equals(currentUser.getUsername()) && userRepository.existsByUsername(request.getUsername()))
            throw new UsernameAlreadyExistsException("Username already exists");

        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (request.getUsername() != null) {
            currentUser.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            currentUser.setEmail(request.getEmail());
        }

        userRepository.save(currentUser);
    }
}
