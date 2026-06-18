-- V4__add_missing_columns_to_mission_results.sql
ALTER TABLE mission_results
    ADD COLUMN IF NOT EXISTS error_code   VARCHAR(255) NULL,
    ADD COLUMN IF NOT EXISTS warning_code VARCHAR(255) NULL;