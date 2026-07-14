package com.janwypych.ProjectManagementApi.repositories.workspaceMember;

import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
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

    Page<WorkspaceMember> findAllByWorkspace(Workspace workspace, Pageable pageable);
}
