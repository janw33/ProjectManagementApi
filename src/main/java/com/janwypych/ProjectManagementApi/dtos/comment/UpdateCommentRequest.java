package com.janwypych.ProjectManagementApi.dtos.comment;

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
public class UpdateCommentRequest {
    @Size(max = 2000, message = "Content must be at most 2000 characters long")
    @Pattern(
            regexp = "^(?!\\s+$).*$",
            message = "Content cannot contain only whitespace"
    )
    private String content;
}
