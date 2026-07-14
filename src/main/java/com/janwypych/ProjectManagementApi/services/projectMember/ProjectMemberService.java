package com.janwypych.ProjectManagementApi.services.projectMember;

import com.janwypych.ProjectManagementApi.dtos.projectMember.CreateProjectMemberRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import org.springframework.stereotype.Service;

//@Service
//public class ProjectMemberService {
//    public Void createProjectMember(User currentUser, CreateProjectMemberRequest request, Long workspaceId, Long projectId) {
//        WorkspaceMember currentMember = workspaceMemberRepository
//                .findByWorkspaceIdAndUser(workspaceId, currentUser)
//                .orElseThrow(WorkspaceNotFoundException::new);
//
//        Workspace workspace = currentMember.getWorkspace();
//
//
//    }
//}
