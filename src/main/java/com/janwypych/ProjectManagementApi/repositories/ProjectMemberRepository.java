package com.janwypych.ProjectManagementApi.repositories;

import com.janwypych.ProjectManagementApi.entities.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findAllByUserAndProject_Workspace(User user, Workspace workspace);
}
