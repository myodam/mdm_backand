-- 1. story_scenes 테이블 생성
CREATE TABLE IF NOT EXISTS story_scenes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    story_id      VARCHAR(50)  NOT NULL,
    scene_id      VARCHAR(50)  NOT NULL,
    mission_type  VARCHAR(50)  NOT NULL,
    next_scene_id VARCHAR(50)  NULL,
    scene_order   INT          NOT NULL,
    UNIQUE KEY uq_story_scene (story_id, scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; -- 여기에 반드시 세미콜론이 있어야 합니다.

-- 2. 데이터 삽입
INSERT INTO story_scenes (story_id, scene_id, mission_type, next_scene_id, scene_order) VALUES
('heungbu_nolbu', 'scene_000', 'none',            'scene_001', 0),
('heungbu_nolbu', 'scene_001', 'protect_swallow', 'scene_002', 1),
('heungbu_nolbu', 'scene_002', 'receive_seed',    'scene_003', 2),
('heungbu_nolbu', 'scene_003', 'open_gourd',       NULL,       3);

-- 3. mission_results 테이블 생성
CREATE TABLE IF NOT EXISTS mission_results (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    story_id      VARCHAR(50)   NOT NULL,
    scene_id      VARCHAR(50)   NOT NULL,
    mission_type  VARCHAR(50)   NOT NULL,
    success       TINYINT(1)    NOT NULL -- (이후 내용)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; -- 여기도 세미콜론 필수