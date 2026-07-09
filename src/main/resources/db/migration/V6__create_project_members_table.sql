CREATE TABLE project_members (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_project_members_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_project_members_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_project_member
        UNIQUE (project_id, user_id)
);