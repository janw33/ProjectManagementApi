CREATE TABLE comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    task_id BIGINT NOT NULL,
    project_member_id BIGINT NOT NULL,

    CONSTRAINT fk_comments_task
    FOREIGN KEY (task_id)
    REFERENCES tasks(id)
    DELETE CASCADE,

    CONSTRAINT fk_comments_author
    FOREIGN KEY (project_member_id)
    REFERENCES project_members(id)
);