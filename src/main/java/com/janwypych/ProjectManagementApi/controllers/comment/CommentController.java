package com.janwypych.ProjectManagementApi.controllers.comment;

import com.janwypych.ProjectManagementApi.dtos.comment.CommentResponse;
import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.dtos.comment.UpdateCommentRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.comment.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateCommentRequest request,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        commentService.createComment(currentUser, request, workspaceId, projectId, taskId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getComments(currentUser, workspaceId, projectId, taskId, pageable));
    }

    @PatchMapping(path = "/{commentId}")
    public ResponseEntity<Void> updateComment(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UpdateCommentRequest request,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.updateComment(currentUser, request, workspaceId, projectId, taskId, commentId);
        return ResponseEntity.ok().build();
    }
}
