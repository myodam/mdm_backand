package com.ux.mbm.mission.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 기획 문서 §4.4: Backend → Unity 공통 응답 필드
 *
 * 백엔드는 AI 서버 응답을 기반으로 아래 모든 필드를 생성하여 Unity에 반환합니다.
 * AI 서버가 반환하는 message, nextAction, nextSceneId는 포함하지 않고,
 * 백엔드가 직접 결정합니다.
 *
 * 성공 예시:
 * {
 *   "success": true, "sceneCleared": true,
 *   "currentSceneId": "scene_001", "nextSceneId": "scene_002",
 *   "nextAction": "NEXT_SCENE", "score": 0.88,
 *   "reasonCode": "MISSION_SUCCESS",
 *   "message": "좋아요! 제비를 조심스럽게 보호했어요.",
 *   "errorCode": null, "warningCode": null
 * }
 *
 * 엔딩 예시: nextSceneId=null, nextAction="ENDING"
 * 실패 예시: nextSceneId=null, nextAction="RETRY"
 */
@Getter
@Builder
public class MissionResultResponse {

    /** 동작 판정 성공 여부 */
    private boolean success;

    /** 현재 씬 클리어 여부 */
    private boolean sceneCleared;

    /** 현재 씬 ID */
    private String currentSceneId;

    /** 다음 씬 ID (실패 또는 엔딩이면 null) */
    private String nextSceneId;

    /**
     * 다음 동작 지시
     * - NEXT_SCENE: 다음 씬으로 이동
     * - RETRY:      현재 씬에서 재시도
     * - ENDING:     엔딩 화면으로 이동
     */
    private String nextAction;

    /** AI 동작 판정 점수 (0.0 ~ 1.0) */
    private double score;

    /**
     * 동작 판정 이유 코드 (AI 서버 반환값을 그대로 전달)
     * 성공: MISSION_SUCCESS
     * 실패: HANDS_TOO_FAR, HANDS_NOT_CENTERED, HAND_NOT_RAISED, MOVEMENT_TOO_SMALL, LOW_SCORE
     */
    private String reasonCode;

    /** 백엔드가 reasonCode/errorCode 기준으로 생성한 사용자 안내 메시지 */
    private String message;

    /**
     * 판정 불가 또는 시스템 오류 코드
     * USER_NOT_DETECTED, HAND_NOT_VISIBLE, INVALID_POSE_DATA, UNKNOWN_MISSION_TYPE,
     * MISSION_MISMATCH, AI_SERVER_ERROR, INTERNAL_SERVER_ERROR
     */
    private String errorCode;

    /**
     * 경고 코드 (사용자 진행은 가능하지만 기록 필요한 경우)
     * SAVE_FAILED: AI 판정 성공 후 DB 저장 실패
     */
    private String warningCode;
}
