package com.janwypych.ProjectManagementApi.exceptions.error;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> validationErrors;
    private String path;
}