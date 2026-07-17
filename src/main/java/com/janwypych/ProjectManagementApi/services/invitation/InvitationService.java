package com.janwypych.ProjectManagementApi.services.invitation;

import com.janwypych.ProjectManagementApi.dtos.invitation.CreateInvitationRequest;
import com.janwypych.ProjectManagementApi.dtos.invitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.SentInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.invitation.PendingInvitationAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.user.UserNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.UserAlreadyWorkspaceMemberException;
import com.janwypych.ProjectManagementApi.mappers.invitation.InvitationMapper;
import com.janwypych.ProjectManagementApi.repositories.invitation.InvitationRepository;
import com.janwypych.ProjectManagementApi.repositories.user.UserRepository;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InvitationService {
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;

    public InvitationService(UserRepository userRepository, WorkspaceMemberRepository workspaceMemberRepository, InvitationRepository invitationRepository, InvitationMapper invitationMapper) {
        this.userRepository = userRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
    }

    public InvitationIdResponse createInvitation(User currentUser, CreateInvitationRequest request, Long workspaceId) {
        WorkspaceMember senderMember = workspaceMemberRepository.findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = senderMember.getWorkspace();

        User receiver = userRepository.findById(request.getReceiverUserId())
                .orElseThrow(UserNotFoundException::new);

        if(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, receiver)) {
            throw new UserAlreadyWorkspaceMemberException(); }

        Optional<Invitation> pendingInvitation = invitationRepository.findByWorkspaceAndReceiverUserAndStatus(workspace, receiver, InvitationStatus.PENDING);

        if (pendingInvitation.isPresent()) {
            throw new PendingInvitationAlreadyExistsException(); }

        Invitation invitation = Invitation.builder()
                .status(InvitationStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .workspace(workspace)
                .senderWorkspaceMember(senderMember)
                .receiverUser(receiver)
                .build();

        Invitation savedInvitation = invitationRepository.save(invitation);

        return invitationMapper.toIdResponse(savedInvitation);
    }

    public Page<SentInvitationSummaryResponse> getSentInvitations(User currentUser, Long workspaceId, Pageable pageable) {
        WorkspaceMember senderMember = workspaceMemberRepository.findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = senderMember.getWorkspace();

        Page<Invitation> invitations = invitationRepository.findAllByWorkspace(workspace, pageable);

        return invitations.map(invitationMapper::toSentSummaryResponse);
    }
}
