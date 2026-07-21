package com.janwypych.ProjectManagementApi.services.user;

import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.dtos.user.UpdateCurrentUserRequest;
import com.janwypych.ProjectManagementApi.dtos.user.UserResponse;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.auth.EmailAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.auth.UsernameAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.mappers.user.UserMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper, InvitationRepository invitationRepository, InvitationMapper invitationMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
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


    public Page<ReceivedInvitationSummaryResponse> getReceivedInvitations(User currentUser, Pageable pageable) {
        Page<Invitation> receivedInvitations = invitationRepository.findAllByReceiverUser(currentUser, pageable);
        return  receivedInvitations.map(invitationMapper::toReceivedInvitationSummaryResponse);
    }

    public ReceivedInvitationDetailsResponse getReceivedInvitation(User currentUser, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndReceiverUser(currentUser, invitationId)
                .orElseThrow(InvitationNotFoundException::new);

        return invitationMapper.toReceivedDetailsResponse(invitation);
    }
}
