package com.janwypych.ProjectManagementApi.dtos.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWorkspaceResponse {
    private Long id;
}
