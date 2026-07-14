package com.janwypych.ProjectManagementApi.mappers.projectMember;

import com.janwypych.ProjectManagementApi.dtos.projectMember.ProjectMemberResponse;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import org.springframework.stereotype.Component;

@Component
public class ProjectMemberMapper {
    public ProjectMemberResponse toResponse(ProjectMember projectMember) {
        return ProjectMemberResponse.builder()
                .id(projectMember.getId())
                .username(projectMember.getWorkspaceMember().getUser().getUsername())
                .workspaceRole(projectMember.getWorkspaceMember().getRole())
                .joinedAt(projectMember.getJoinedAt())
                .build();
    }
}
