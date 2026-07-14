package com.janwypych.ProjectManagementApi.mappers.workspaceMember;

import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
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

    public WorkspaceMemberDetailsResponse toDetailsResponse(WorkspaceMember workspaceMember) {
        User user = workspaceMember.getUser();

        return WorkspaceMemberDetailsResponse.builder()
                .id(workspaceMember.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(workspaceMember.getRole())
                .joinedAt(workspaceMember.getJoinedAt())
                .build();
    }
}
