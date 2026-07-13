package com.janwypych.ProjectManagementApi.repositories.projectMember;

import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findAllByWorkspaceMemberAndProject_Workspace(WorkspaceMember member, Workspace workspace);
    Optional<ProjectMember> findByIdAndProject(Long id, Project project);
    Optional<ProjectMember> findByWorkspaceMemberAndProject(WorkspaceMember member, Project project);
}
