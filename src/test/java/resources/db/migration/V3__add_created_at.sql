-- V3__add_created_at.sql 수정 예시
ALTER TABLE mission_results
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;