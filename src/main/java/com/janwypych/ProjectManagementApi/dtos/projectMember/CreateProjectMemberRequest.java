package com.janwypych.ProjectManagementApi.dtos.projectMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProjectMemberRequest {
    private Long workspaceMemberId;
}
