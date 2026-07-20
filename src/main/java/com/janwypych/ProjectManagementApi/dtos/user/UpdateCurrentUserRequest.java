package com.janwypych.ProjectManagementApi.dtos.user;

import jakarta.validation.constraints.Email;
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
public class UpdateCurrentUserRequest {
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9_]*[a-zA-Z0-9])?$",
            message = "Username must contain only letters, numbers and underscores"
    )
    private String username;

    @Size(max = 100, message = "Email must be at most 100 characters long")
    @Email(message = "Email must be a valid email address")
    private String email;

}
