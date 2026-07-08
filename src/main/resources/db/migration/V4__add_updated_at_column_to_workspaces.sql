ALTER TABLE workspaces
ADD COLUMN updated_at TIMESTAMP;

UPDATE workspaces
SET updated_at = created_at
WHERE updated_at IS NULL;

ALTER TABLE workspaces
ALTER COLUMN updated_at SET NOT NULL;