-- V8: 전체 씬 fail_messages 최신화

UPDATE story_scenes
SET fail_messages = '{"BOOK_NOT_TURNED":"왼손으로 책장을 오른쪽으로 넘겨주세요.","HANDS_TOO_FAR":"책은 더 가까이서 잡아 주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_000';

UPDATE story_scenes
SET fail_messages = '{"HANDS_TOO_FAR":"제비의 다리를 가운데에 두고 둘둘 말아주세요.","HANDS_NOT_CENTERED":"두 손을 몸 가운데로 모아주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_001';

UPDATE story_scenes
SET fail_messages = '{"HANDS_TOO_FAR":"제비의 다리를 가운데에 두고 붕대로 둘둘 말아주세요.","HANDS_NOT_CENTERED":"두 손을 몸 가운데로 모아주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_002';

UPDATE story_scenes
SET fail_messages = '{"HANDS_TOO_FAR":"우리 친구 카메라 앞에 서서 다시 해볼까요?","ARMS_NOT_WIDE":"양팔을 더 크게 벌려주세요.","MOVEMENT_TOO_SMALL":"팔을 더 크게 움직여주세요.","LOW_SCORE":"조금 더 크게 동작해볼까요?","USER_NOT_DETECTED":"카메라 앞에 서서 다시 시도해주세요.","HAND_NOT_VISIBLE":"손이 화면 안에 보이도록 해주세요.","HAND_POSITION_TOO_HIGH":"손을 조금 더 아래로 내려서 움직여주세요.","SAWING_MOTION_TOO_SMALL":"톱질하듯이 양손을 번갈아 움직여주세요."}'
WHERE story_id = 'heungbu_nolbu' AND scene_id = 'scene_003';
