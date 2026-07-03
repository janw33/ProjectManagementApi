package com.janwypych.ProjectManagementApi.entities;

import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceRole role;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}