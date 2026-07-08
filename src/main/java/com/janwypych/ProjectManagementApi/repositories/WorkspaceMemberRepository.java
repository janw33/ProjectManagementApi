package com.janwypych.ProjectManagementApi.repositories;

import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    Page<WorkspaceMember> findAllByUser(User currentUser, Pageable pageable);
    Optional<WorkspaceMember> findByWorkspaceIdAndUser(Long workspaceId, User user);
}
