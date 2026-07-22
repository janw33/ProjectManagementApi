CREATE TABLE invitations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    status varchar(20) NOT NULL,

    workspace_id BIGINT NOT NULL,
    sender_workspace_member_id BIGINT NOT NULL,
    receiver_user_id BIGINT NOT NULL,

    CONSTRAINT fk_invitations_workspace
    FOREIGN KEY (workspace_id)
    REFERENCES workspaces(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_invitations_sender_workspace_member
    FOREIGN KEY (sender_workspace_member_id)
    REFERENCES workspace_members(id),

    CONSTRAINT fk_invitations_receiver_user
    FOREIGN KEY (receiver_user_id)
    REFERENCES users(id)
);