package com.janwypych.ProjectManagementApi.repositories.comment;

import com.janwypych.ProjectManagementApi.entities.comment.Comment;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByTask(Task task, Pageable pageable);
}
