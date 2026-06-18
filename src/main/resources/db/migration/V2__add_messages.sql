-- =============================================================
-- V2: story_scenes 메시지 컬럼 추가 (MySQL용)
-- =============================================================

ALTER TABLE story_scenes
    ADD COLUMN success_message VARCHAR(255) NULL COMMENT 'MISSION_SUCCESS 시 사용자 메시지',
    ADD COLUMN fail_messages   TEXT         NULL COMMENT 'reasonCode → 메시지 JSON',
    ADD COLUMN before_message  VARCHAR(255) NULL COMMENT '미션 시작 전 안내 메시지';

-- scene_000: 튜토리얼
UPDATE story_scenes
SET before_message  = '몸을 카메라 앞에 세우고 준비해보세요!',
    success_message = '마법의 도서관에 오신 것을 환영합니다. 책을 펼쳐 이야기 속 주인공이 되어 볼까요?',
    fail_messages   = NULL
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_000';

-- scene_001: 다친 제비 보호하기
UPDATE story_scenes
SET before_message  = '저런, 제비가 다쳤나봐요! 아픈 제비를 위해 다리에 붕대를 감아주세요!',
    success_message = '잘했어요! 덕분에 제비가 곧 건강하게 날아갈 수 있겠어요!',
    fail_messages   = '{"HANDS_TOO_FAR":"제비의 다리를 가운데에 두고 둘둘 말아주세요.","HANDS_NOT_CENTERED":"두 손을 몸 가운데로 모아주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_001';

-- scene_002: 박씨 받기
UPDATE story_scenes
SET before_message  = '은혜를 입은 제비가 흥부에게 보은을 하려나봐요! 손을 내밀어봐요',
    success_message = '성공이에요! 이 씨앗은 어떤 열매의 씨앗일까요?',
    fail_messages   = '{"HANDS_TOO_FAR":"두 손을 조금 더 가까이 모아주세요.","HAND_NOT_RAISED":"한 손을 어깨보다 높게 들어주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_002';

-- scene_003: 박 타기
UPDATE story_scenes
SET before_message  = '제비가 준 씨앗이 커다란 박으로 자랐어요! 어서 잘라보아요',
    success_message = '우와아! 박에서 금은보화가 쏟아져 나와요!',
    fail_messages   = '{"HANDS_TOO_FAR":"우리 친구 카메라 앞에 서서 다시 해볼까요?.","ARMS_NOT_WIDE":"양팔을 더 크게 벌려주세요.","MOVEMENT_TOO_SMALL":"팔을 더 크게 움직여주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_003';