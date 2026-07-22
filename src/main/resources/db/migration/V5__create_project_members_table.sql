CREATE TABLE project_members (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT NOT NULL,
    workspace_member_id BIGINT NOT NULL,

    CONSTRAINT fk_project_members_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_project_members_workspace_member
        FOREIGN KEY (workspace_member_id)
        REFERENCES workspace_members(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_project_members_project_workspace_member
        UNIQUE (project_id, workspace_member_id)
);