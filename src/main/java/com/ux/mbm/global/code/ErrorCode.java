package com.ux.mbm.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 기획 문서 §11: errorCode 정의
 *
 * errorCode는 동작이 부족한 경우가 아니라,
 * 판정 자체가 어렵거나 시스템 문제가 있을 때 사용합니다.
 *
 * - AI 서버 반환: USER_NOT_DETECTED, HAND_NOT_VISIBLE, INVALID_POSE_DATA
 * - 백엔드 반환:  INVALID_POSE_DATA, MISSION_MISMATCH, UNKNOWN_MISSION_TYPE, AI_SERVER_ERROR, INTERNAL_SERVER_ERROR
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /** 카메라 화면에서 사용자 감지 실패 (AI 서버 반환) */
    USER_NOT_DETECTED("카메라 앞에 서서 다시 시도해주세요."),

    /** 손목 좌표 미감지 (AI 서버 반환) */
    HAND_NOT_VISIBLE("손이 화면 안에 보이도록 해주세요."),

    /** poseFrames 누락 또는 좌표 구조 오류 (백엔드 1차 검증 또는 AI 서버 반환) */
    INVALID_POSE_DATA("동작 정보를 확인할 수 없어요. 다시 시도해주세요."),

    /** storyId + sceneId 조합 불일치 (백엔드 반환) */
    MISSION_MISMATCH("현재 장면의 미션 정보가 올바르지 않습니다."),

    /** 요청한 missionType이 해당 씬에 등록되지 않은 타입 (백엔드 반환) */
    UNKNOWN_MISSION_TYPE("지원하지 않는 미션 타입입니다."),

    /** AI 서버 호출 실패 또는 내부 오류 (백엔드 반환) */
    AI_SERVER_ERROR("잠시 문제가 발생했어요. 다시 시도해주세요."),

    /** 백엔드 내부 예외 (백엔드 반환) */
    INTERNAL_SERVER_ERROR("잠시 문제가 발생했어요. 다시 시도해주세요.");

    private final String message;
}
