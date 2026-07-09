package com.janwypych.ProjectManagementApi.repositories.workspace;

import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository <Workspace, Long> {
}
