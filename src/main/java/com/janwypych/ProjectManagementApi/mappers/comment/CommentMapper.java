package com.janwypych.ProjectManagementApi.mappers.comment;

import com.janwypych.ProjectManagementApi.dtos.comment.CommentResponse;
import com.janwypych.ProjectManagementApi.dtos.comment.CreateCommentRequest;
import com.janwypych.ProjectManagementApi.entities.comment.Comment;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toEntity(CreateCommentRequest request, ProjectMember author, Task task) {
        return Comment.builder()
                .content(request.getContent())
                .author(author)
                .task(task)
                .build();
    }

    public CommentResponse toResponse(Comment comment) {
        boolean edited = comment.getUpdatedAt().isAfter(comment.getCreatedAt());

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .edited(edited)
                .authorUsername(comment.getAuthor().getWorkspaceMember().getUser().getUsername())
                .build();
    }
}
