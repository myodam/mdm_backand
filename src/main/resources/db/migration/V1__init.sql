-- =============================================================
-- 몸으로 넘기는 전래동화 - 흥부와 놀부 MVP
-- Flyway 마이그레이션 V1: 초기 스키마 및 데이터
-- =============================================================

-- -------------------------------------------------------
-- 동화별 씬/미션 매핑 테이블
-- 새 동화 추가 시 INSERT만 하면 됨 (코드 배포 불필요)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS story_scenes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    story_id      VARCHAR(50)  NOT NULL,
    scene_id      VARCHAR(50)  NOT NULL,
    mission_type  VARCHAR(50)  NOT NULL,
    next_scene_id VARCHAR(50)  NULL,        -- null = 마지막 씬 → ENDING
    scene_order   INT          NOT NULL,
    UNIQUE KEY uq_story_scene (story_id, scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 흥부와 놀부 초기 씬 데이터
INSERT INTO story_scenes (story_id, scene_id, mission_type, next_scene_id, scene_order) VALUES
('heungbu_nolbu', 'opening',   'none',            'scene_001', 1), -- 오프닝 추가
('heungbu_nolbu', 'scene_001', 'protect_swallow', 'scene_002', 1),
('heungbu_nolbu', 'scene_002', 'receive_seed',    'scene_003', 2),
('heungbu_nolbu', 'scene_003', 'open_gourd',       NULL,       3);

-- -------------------------------------------------------
-- 미션 판정 결과 저장
-- userId 없음 (로그인 미구현), 좌표 원본 미저장
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS mission_results (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    story_id      VARCHAR(50)   NOT NULL,
    scene_id      VARCHAR(50)   NOT NULL,
    mission_type  VARCHAR(50)   NOT NULL,
    success       TINYINT(1)    NOT NULL DEFAULT 0,
    score         DOUBLE        NOT NULL DEFAULT 0,
    reason_code   VARCHAR(50)   NULL,
    message       VARCHAR(255)  NULL,
    error_code    VARCHAR(50)   NULL,
    warning_code  VARCHAR(50)   NULL,
    created_at    DATETIME(6)   NOT NULL,
    INDEX idx_mission_scene (story_id, scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
