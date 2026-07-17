package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.dtos.user.UserResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import org.springframework.stereotype.Service;

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
}
