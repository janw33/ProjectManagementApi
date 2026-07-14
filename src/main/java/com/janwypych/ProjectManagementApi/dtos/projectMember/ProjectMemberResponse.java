package com.janwypych.ProjectManagementApi.dtos.projectMember;

import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectMemberResponse {
    private Long id;
    private String username;
    private WorkspaceRole workspaceRole;
    private LocalDateTime joinedAt;
}
