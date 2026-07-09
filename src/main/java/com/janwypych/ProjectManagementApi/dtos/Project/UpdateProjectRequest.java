package com.janwypych.ProjectManagementApi.dtos.Project;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateProjectRequest {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(
            regexp = "^(?!\\s+$).*$",
            message = "Name cannot contain only whitespace"
    )
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters long")
    @Pattern(
            regexp = "^(?!\\s+$).*$",
            message = "Description cannot contain only whitespace"
    )
    private String description;
}
