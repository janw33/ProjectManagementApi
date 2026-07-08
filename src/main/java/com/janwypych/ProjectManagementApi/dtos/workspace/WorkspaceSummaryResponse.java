package com.janwypych.ProjectManagementApi.dtos.workspace;

import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceSummaryResponse {
    private Long id;
    private String name;
    private WorkspaceRole role;
}
