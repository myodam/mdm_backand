package com.ux.mbm.global.code;

/**
 * 기획 문서 §10: reasonCode 정의
 *
 * AI 서버가 반환하는 판정 이유 코드 목록입니다.
 * 메시지는 story_scenes.fail_messages(DB)에서 관리합니다.
 */
public enum ReasonCode {

    // ── 공통 ──────────────────────────────────────────────
    MISSION_SUCCESS,
    LOW_SCORE,

    // ── Scene 0: skip_book ────────────────────────────────
    BOOK_NOT_TURNED,

    // ── Scene 1: protect_swallow ──────────────────────────
    HANDS_TOO_FAR,
    HANDS_NOT_CENTERED,

    // ── Scene 2: receive_seed ─────────────────────────────
    HAND_NOT_RAISED,
    HANDS_NOT_TOGETHER,

    // ── Scene 3: open_gourd ───────────────────────────────
    ARMS_NOT_WIDE,
    MOVEMENT_TOO_SMALL,
    HAND_POSITION_TOO_HIGH,
    SAWING_MOTION_TOO_SMALL
}
