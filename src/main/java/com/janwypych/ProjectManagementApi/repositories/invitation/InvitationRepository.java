package com.janwypych.ProjectManagementApi.repositories.invitation;

import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByWorkspaceAndReceiverUserAndStatus(Workspace workspace, User receiver, InvitationStatus invitationStatus);

    Page<Invitation> findAllByWorkspace(Workspace workspace, Pageable pageable);

    Optional<Invitation> findByIdAndWorkspace(Long invitationId, Workspace workspace);

    Page<Invitation> findAllByReceiverUser(User currentUser, Pageable pageable);

    Optional<Invitation> findByIdAndReceiverUser(User currentUser, Long invitationId);
}
