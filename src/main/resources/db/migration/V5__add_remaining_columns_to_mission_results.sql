-- V5: mission_results 누락 컬럼 추가 (score, reason_code, message)
ALTER TABLE mission_results
    ADD COLUMN score       DOUBLE       NOT NULL DEFAULT 0.0,
    ADD COLUMN reason_code VARCHAR(50)  NULL,
    ADD COLUMN message     VARCHAR(255) NULL;
