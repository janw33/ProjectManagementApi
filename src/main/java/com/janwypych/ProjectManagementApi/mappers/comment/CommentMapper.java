package com.janwypych.ProjectManagementApi.mappers.comment;

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
}
