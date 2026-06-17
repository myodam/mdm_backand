package com.ux.mbm.global.code;

import java.util.Map;

/**
 * 기획 문서 §10: reasonCode / errorCode → 사용자 메시지 변환
 *
 * AI 서버는 reasonCode / errorCode만 반환하고,
 * 백엔드가 이를 사용자 안내 문구로 변환합니다.
 *
 * MISSION_SUCCESS 메시지는 storyId + sceneId 조합으로 장면별로 다르게 관리합니다.
 */
public final class MessageMapper {

    private MessageMapper() {}

    /**
     * 기획 문서 §10.1: MISSION_SUCCESS 메시지는 storyId + sceneId 기준
     * - heungbu_nolbu/scene_001 → "좋아요! 제비를 조심스럽게 보호했어요."
     * - heungbu_nolbu/scene_002 → "잘했어요! 박씨를 받았어요."
     * - heungbu_nolbu/scene_003 → "힘차게 박을 탔어요! 박이 열렸어요."
     */
    private static final Map<String, String> SUCCESS_MESSAGE_MAP = Map.of(
            "heungbu_nolbu/scene_001", "좋아요! 제비를 조심스럽게 보호했어요.",
            "heungbu_nolbu/scene_002", "잘했어요! 박씨를 받았어요.",
            "heungbu_nolbu/scene_003", "힘차게 박을 탔어요! 박이 열렸어요."
    );

    /**
     * reasonCode → 사용자 메시지 변환
     *
     * @param reasonCode AI 서버가 반환한 reasonCode
     * @param storyId    현재 동화 ID
     * @param sceneId    현재 씬 ID
     * @return 사용자에게 보여줄 안내 메시지
     */
    public static String fromReasonCode(String reasonCode, String storyId, String sceneId) {
        if (reasonCode == null) {
            return ErrorCode.INTERNAL_SERVER_ERROR.getMessage();
        }

        if (ReasonCode.MISSION_SUCCESS.name().equals(reasonCode)) {
            String key = storyId + "/" + sceneId;
            return SUCCESS_MESSAGE_MAP.getOrDefault(key, "잘했어요!");
        }

        try {
            String message = ReasonCode.valueOf(reasonCode).getMessage();
            return message != null ? message : "다시 시도해주세요.";
        } catch (IllegalArgumentException e) {
            return "다시 시도해주세요.";
        }
    }

    /**
     * errorCode → 사용자 메시지 변환
     *
     * @param errorCode AI 서버 또는 백엔드가 반환한 errorCode
     * @return 사용자에게 보여줄 안내 메시지
     */
    public static String fromErrorCode(String errorCode) {
        if (errorCode == null) {
            return ErrorCode.INTERNAL_SERVER_ERROR.getMessage();
        }
        try {
            return ErrorCode.valueOf(errorCode).getMessage();
        } catch (IllegalArgumentException e) {
            return ErrorCode.INTERNAL_SERVER_ERROR.getMessage();
        }
    }
}
