package com.janwypych.ProjectManagementApi.repositories;

import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    @Query("""
    SELECT wm
    FROM WorkspaceMember wm
    WHERE wm.user = :user
    ORDER BY wm.workspace.updatedAt DESC
""")
    Page<WorkspaceMember> findAllByUserOrderByWorkspaceUpdatedAtDesc(
            @Param("user") User user,
            Pageable pageable
    );

    Optional<WorkspaceMember> findByWorkspaceIdAndUser(Long workspaceId, User user);
}
