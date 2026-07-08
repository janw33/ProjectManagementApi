package com.janwypych.ProjectManagementApi.dtos.workspace;

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
public class WorkspaceDetailsResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private WorkspaceRole role;
}
