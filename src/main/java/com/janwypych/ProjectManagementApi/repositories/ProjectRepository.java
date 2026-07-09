package com.janwypych.ProjectManagementApi.repositories;

import com.janwypych.ProjectManagementApi.entities.Project;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAllByWorkspaceOrderByUpdatedAtDesc(Workspace workspace, Pageable pageable);
    Optional<Project> findByIdAndWorkspace(Long id, Workspace workspace);
}
