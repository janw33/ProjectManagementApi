package com.janwypych.ProjectManagementApi.dtos.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequest {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Description must be at most 2000 characters long")
    @Pattern(
            regexp = "^(?!\\s+$).*$",
            message = "Description cannot contain only whitespace"
    )
    private String description;

    @NotNull(message = "Assignee id cannot be null")
    private Long assigneeProjectMemberId;
}
