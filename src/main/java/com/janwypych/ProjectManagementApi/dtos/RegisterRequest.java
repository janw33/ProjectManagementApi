package com.janwypych.ProjectManagementApi.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(
        min = 3,
        max = 30,
        message = "Username must be between 3 and 30 characters"
    )
    @Pattern(
        regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9_]*[a-zA-Z0-9])?$",
        message = "Username must contain only letters, numbers and underscores"
    )
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 100, message = "Email must be at most 100 characters long")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    //dodaj regex
    private String password;
}
