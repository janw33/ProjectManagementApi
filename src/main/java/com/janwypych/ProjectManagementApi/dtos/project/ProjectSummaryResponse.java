package com.janwypych.ProjectManagementApi.dtos.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectSummaryResponse {
    private Long id;
    private String name;
    private boolean hasAccess;
}
