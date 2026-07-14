package com.janwypych.ProjectManagementApi.dtos.projectMember;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProjectMemberRequest {
    @NotNull(message = "Workspace member id cannot be null")
    private Long workspaceMemberId;
}
