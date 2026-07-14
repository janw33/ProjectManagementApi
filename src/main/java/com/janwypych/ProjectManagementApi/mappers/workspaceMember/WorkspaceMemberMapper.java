package com.janwypych.ProjectManagementApi.mappers.workspaceMember;

import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMemberMapper {
    public WorkspaceMemberSummaryResponse toSummaryResponse(WorkspaceMember workspaceMember) {
        return WorkspaceMemberSummaryResponse.builder()
                .id(workspaceMember.getId())
                .username(workspaceMember.getUser().getUsername())
                .role(workspaceMember.getRole())
                .build();
    }
}
