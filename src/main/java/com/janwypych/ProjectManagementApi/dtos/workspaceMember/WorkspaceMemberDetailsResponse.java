package com.janwypych.ProjectManagementApi.dtos.workspaceMember;

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
public class WorkspaceMemberDetailsResponse {
    private Long id;
    private String username;
    private String email;
    private WorkspaceRole role;
    private LocalDateTime joinedAt;
}
