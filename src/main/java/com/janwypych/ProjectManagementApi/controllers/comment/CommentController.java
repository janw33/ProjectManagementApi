package com.janwypych.ProjectManagementApi.controllers.comment;

import com.janwypych.ProjectManagementApi.dtos.comment.CommentIdResponse;
import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.comment.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentIdResponse> createComment(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateCommentRequest request,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(currentUser, request, workspaceId, projectId, taskId);
    }
}
