-- V5: Add missing BaseEntity columns to newsletter_subscribers

ALTER TABLE newsletter_subscribers ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE newsletter_subscribers ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE newsletter_subscribers ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;
