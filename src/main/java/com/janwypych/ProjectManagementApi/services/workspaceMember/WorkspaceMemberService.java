package com.janwypych.ProjectManagementApi.services.workspaceMember;

import com.janwypych.ProjectManagementApi.dtos.workspaceMember.UpdateWorkspaceMemberRequest;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.workspaceMember.WorkspaceMemberMapper;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceMemberService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceMemberMapper workspaceMemberMapper;

    public WorkspaceMemberService(WorkspaceMemberRepository workspaceMemberRepository, WorkspaceMemberMapper workspaceMemberMapper) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceMemberMapper = workspaceMemberMapper;
    }

    public Page<WorkspaceMemberSummaryResponse> getWorkspaceMembers(User currentUser, Long workspaceId, Pageable pageable) {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = member.getWorkspace();

        Page<WorkspaceMember> members = workspaceMemberRepository.findAllByWorkspace(workspace, pageable);

        return members.map(workspaceMemberMapper::toSummaryResponse);
    }

    public WorkspaceMemberDetailsResponse getWorkspaceMember(User currentUser, Long workspaceId, Long memberId) {
        WorkspaceMember currentMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = currentMember.getWorkspace();

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByIdAndWorkspace(memberId, workspace)
                .orElseThrow(WorkspaceMemberNotFoundException::new);

        return workspaceMemberMapper.toDetailsResponse(workspaceMember);
    }

    @Transactional
    public void updateWorkspaceMember(User currentUser, UpdateWorkspaceMemberRequest request, Long workspaceId, Long memberId) {
        WorkspaceMember currentMember = workspaceMemberRepository
                .findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(WorkspaceNotFoundException::new);

        Workspace workspace = currentMember.getWorkspace();

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByIdAndWorkspace(memberId, workspace)
                .orElseThrow(WorkspaceMemberNotFoundException::new);

        if(request.getRole() != null) {
            workspaceMember.setRole(request.getRole());
        }
    }
}
