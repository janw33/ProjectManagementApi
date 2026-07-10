package com.janwypych.ProjectManagementApi.repositories.task;

import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByProject(Project project, Pageable pageable);
}
