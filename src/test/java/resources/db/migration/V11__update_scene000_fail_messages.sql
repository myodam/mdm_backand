-- V11: scene_000 fail_messages 추가
UPDATE story_scenes
SET fail_messages = '{"BOOK_NOT_TURNED":"왼손으로 책장을 오른쪽으로 넘겨주세요.","HANDS_TOO_FAR":"책은 더 가까이서 잡아 주세요.","MOVEMENT_TOO_SMALL":"손을 조금 더 크게 움직여주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요.","HANDS_NOT_TOGETHER":"두 손을 모아서 씨앗을 받아주세요.","HAND_POSITION_TOO_HIGH":"손을 조금 더 아래로 내려서 움직여주세요.","SAWING_MOTION_TOO_SMALL ":"톱질하듯이 양손을 번갈아 움직여주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_000';
