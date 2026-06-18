package com.ux.mbm.global.ai;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기획 문서 §10: AI Server → Backend 응답 바디
 *
 * AI 서버는 동작 판정 결과만 반환합니다.
 * message, nextAction, nextSceneId는 포함하지 않습니다.
 * 이 세 값은 백엔드가 결정합니다.
 *
 * 성공 예시:
 * { "success": true,  "score": 0.88, "reasonCode": "MISSION_SUCCESS", "errorCode": null }
 *
 * 실패 예시:
 * { "success": false, "score": 0.42, "reasonCode": "HANDS_TOO_FAR",   "errorCode": null }
 *
 * 예외 예시:
 * { "success": false, "score": 0.0,  "reasonCode": null, "errorCode": "USER_NOT_DETECTED" }
 */
@Getter
@NoArgsConstructor
public class AiServerResponse {

    /** 동작 성공 여부 */
    private boolean success;

    /** 동작 점수 (0.0 ~ 1.0, 기준: >= 0.7 성공) */
    private double score;

    /**
     * 동작 판정 이유 코드 (AI 서버 반환)
     * 성공: MISSION_SUCCESS
     * 실패: HANDS_TOO_FAR, HANDS_NOT_CENTERED, HAND_NOT_RAISED, MOVEMENT_TOO_SMALL, LOW_SCORE
     */
    private String reasonCode;

    /**
     * 예외 코드 (판정 불가 상황)
     * USER_NOT_DETECTED, HAND_NOT_VISIBLE, INVALID_POSE_DATA, UNKNOWN_MISSION_TYPE
     */
    private String errorCode;
}
