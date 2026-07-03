package com.janwypych.ProjectManagementApi.dtos.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWorkspaceResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
