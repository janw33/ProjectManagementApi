package com.janwypych.ProjectManagementApi.dtos.task;

import com.janwypych.ProjectManagementApi.entities.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskSummaryResponse {
    private Long id;
    private String name;
    private TaskStatus status;
    private String assigneeUsername;
}
