-- V9: scene_002 fail_messages에 HAND_NOT_RAISED, HANDS_NOT_TOGETHER 추가
UPDATE story_scenes
SET fail_messages = '{"HANDS_TOO_FAR":"제비의 다리를 가운데에 두고 붕대로 둘둘 말아주세요.","HANDS_NOT_CENTERED":"두 손을 몸 가운데로 모아주세요.","HAND_NOT_RAISED":"두 손을 어깨보다 높게 들어주세요.","HANDS_NOT_TOGETHER":"두 손을 모아서 씨앗을 받아주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_002';
