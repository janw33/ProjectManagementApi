package com.janwypych.ProjectManagementApi.services.invitation;

import com.janwypych.ProjectManagementApi.dtos.invitation.receivedInvitation.ReceivedInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.receivedInvitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.CreateInvitationRequest;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.SentInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.SentInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationAlreadyProcessedException;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationExpiredException;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

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

    public SentInvitationDetailsResponse getSentInvitation(User currentUser, Long workspaceId, Long invitationId) {
        WorkspaceMember senderMember = workspaceMemberRepository.findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = senderMember.getWorkspace();

        Invitation invitation = invitationRepository.findByIdAndWorkspace(invitationId, workspace)
                .orElseThrow(InvitationNotFoundException::new);

        return invitationMapper.toSentDetailsResponse(invitation);
    }

    public Page<ReceivedInvitationSummaryResponse> getReceivedInvitations(User currentUser, Pageable pageable) {
        Page<Invitation> receivedInvitations = invitationRepository.findAllByReceiverUser(currentUser, pageable);
        return  receivedInvitations.map(invitationMapper::toReceivedInvitationSummaryResponse);
    }

    public ReceivedInvitationDetailsResponse getReceivedInvitation(User currentUser, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndReceiverUser(invitationId, currentUser)
                .orElseThrow(InvitationNotFoundException::new);

        return invitationMapper.toReceivedDetailsResponse(invitation);
    }

    @Transactional
    public void acceptInvitation(User currentUser, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndReceiverUser(invitationId, currentUser)
                .orElseThrow(InvitationNotFoundException::new);

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            throw new InvitationExpiredException();
        }

        if (invitation.getStatus() == InvitationStatus.ACCEPTED ||
                invitation.getStatus() == InvitationStatus.DENIED) {
            throw new InvitationAlreadyProcessedException();
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);

        WorkspaceMember newMember = WorkspaceMember.builder()
                .role(WorkspaceRole.MEMBER)
                .workspace(invitation.getWorkspace())
                .user(currentUser)
                .build();

        workspaceMemberRepository.save(newMember);
    }

    @Transactional
    public void declineInvitation(User currentUser, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndReceiverUser(invitationId, currentUser)
                .orElseThrow(InvitationNotFoundException::new);

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            throw new InvitationExpiredException();
        }

        if (invitation.getStatus() == InvitationStatus.ACCEPTED ||
                invitation.getStatus() == InvitationStatus.DENIED) {
            throw new InvitationAlreadyProcessedException();
        }

        invitation.setStatus(InvitationStatus.DENIED);
    }

    public void deleteSentInvitation(User currentUser, Long workspaceId, Long invitationId) {
        WorkspaceMember senderMember = workspaceMemberRepository.findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = senderMember.getWorkspace();

        Invitation invitation = invitationRepository.findByIdAndWorkspace(invitationId, workspace)
                .orElseThrow(InvitationNotFoundException::new);

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyProcessedException();
        }
        invitationRepository.delete(invitation);
    }
}
