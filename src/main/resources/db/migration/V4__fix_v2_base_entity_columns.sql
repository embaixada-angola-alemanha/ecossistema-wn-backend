-- V4: Add missing BaseEntity columns to V2 tables

-- article_versions: add updated_by, change version INT -> BIGINT
ALTER TABLE article_versions ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE article_versions ALTER COLUMN version TYPE BIGINT;

-- editorial_comments: add created_by, updated_by, version
ALTER TABLE editorial_comments ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE editorial_comments ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE editorial_comments ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;
