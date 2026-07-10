package com.janwypych.ProjectManagementApi.entities.task;

import com.janwypych.ProjectManagementApi.entities.enums.TaskStatus;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_project_member_id")
    private ProjectMember assignee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
