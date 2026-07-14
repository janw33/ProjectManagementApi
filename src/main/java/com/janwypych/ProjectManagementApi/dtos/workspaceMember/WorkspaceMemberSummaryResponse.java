package com.janwypych.ProjectManagementApi.dtos.workspaceMember;

import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceMemberSummaryResponse {
    private Long id;
    private String username;
    private WorkspaceRole role;
}
