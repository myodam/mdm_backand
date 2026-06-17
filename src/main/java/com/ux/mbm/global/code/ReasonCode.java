package com.ux.mbm.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 기획 문서 §10: reasonCode 정의
 *
 * reasonCode는 사용자가 동작을 수행했지만
 * 성공 기준에 부족하거나, 성공 조건을 만족했을 때 사용하는 판정 이유 코드입니다.
 *
 * AI 서버가 반환하고, 백엔드는 이를 사용자 메시지로 변환합니다.
 * MISSION_SUCCESS 성공 메시지는 storyId + sceneId 조합으로 별도 관리합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ReasonCode {

    // ── 공통 ──────────────────────────────────────────────
    /** 미션 성공 (메시지는 storyId+sceneId 조합으로 MessageMapper에서 결정) */
    MISSION_SUCCESS(null),

    /** 점수가 기준보다 낮음 */
    LOW_SCORE("조금 더 크게 동작해볼까요?"),

    // ── Scene 1: protect_swallow ───────────────────────────
    /** 양손 사이 거리가 너무 멀다 */
    HANDS_TOO_FAR("두 손을 조금 더 가까이 모아주세요."),

    /** 양손이 몸 중앙에서 벗어남 */
    HANDS_NOT_CENTERED("두 손을 몸 가운데로 모아주세요."),

    // ── Scene 2: receive_seed ──────────────────────────────
    /** 한 손이 충분히 올라가지 않음 */
    HAND_NOT_RAISED("한 손을 어깨보다 높게 들어주세요."),

    // ── Scene 3: open_gourd ───────────────────────────────
    /** 양팔이 충분히 벌어지지 않음 */
    ARMS_NOT_WIDE("양팔을 더 크게 벌려주세요."),

    /** 팔 움직임 변화량이 부족함 */
    MOVEMENT_TOO_SMALL("팔을 더 크게 움직여주세요.");

    private final String message;
}
