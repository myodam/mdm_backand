package com.ux.mbm.mission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 기획 문서 §6.3: Unity → Backend 요청 바디
 *
 * Unity가 미션 수행 후 MediaPipe 좌표를 백엔드로 전송할 때 사용합니다.
 * userId 없음 (로그인 미구현).
 * attemptCount 없음 (기획 제외 항목).
 *
 * 예시:
 * {
 *   "storyId": "heungbu_nolbu",
 *   "sceneId": "scene_001",
 *   "missionType": "protect_swallow",
 *   "captureDurationSec": 5,
 *   "sampleFps": 5,
 *   "poseFrames": [ ... ]
 * }
 */
@Getter
@NoArgsConstructor
public class MissionResultRequest {

    /** 동화 ID. MVP에서는 "heungbu_nolbu" 고정 */
    @NotBlank(message = "storyId는 필수입니다.")
    private String storyId;

    /** 현재 씬 ID (scene_001, scene_002, scene_003) */
    @NotBlank(message = "sceneId는 필수입니다.")
    private String sceneId;

    /** 현재 미션 타입 (protect_swallow, receive_seed, open_gourd) */
    @NotBlank(message = "missionType은 필수입니다.")
    private String missionType;

    /** Unity가 좌표를 수집한 시간 (초 단위, 기본 5초) */
    private int captureDurationSec;

    /** 초당 샘플링한 프레임 수 (기본 5fps → 약 25개 프레임) */
    private int sampleFps;

    /**
     * MediaPipe 포즈 프레임 목록
     * captureDurationSec=5, sampleFps=5 기준 약 25개
     * timestamp: 0.0, 0.2, 0.4, ... 4.8
     */
    @NotEmpty(message = "poseFrames는 필수입니다.")
    private List<PoseFrame> poseFrames;

    // ── 중첩 DTO ──────────────────────────────────────────

    /** 단일 포즈 프레임 */
    @Getter
    @NoArgsConstructor
    public static class PoseFrame {
        /** 미션 시작 후 경과 시간 (초) */
        private double timestamp;

        /**
         * MediaPipe landmark 목록
         * 키: leftShoulder, rightShoulder, leftElbow, rightElbow, leftWrist, rightWrist, nose 등
         */
        private Map<String, Landmark> landmarks;
    }

    /** 단일 랜드마크 좌표 */
    @Getter
    @NoArgsConstructor
    public static class Landmark {
        /** 화면 가로 위치 (0~1). MediaPipe 기준 정규화된 값 */
        private double x;

        /**
         * 화면 세로 위치 (0~1). MediaPipe 기준 정규화된 값.
         * y값이 작을수록 화면 위쪽 (손들기 판정 시 wrist.y < shoulder.y)
         */
        private double y;

        /** 해당 관절이 잘 보이는 정도 (0~1) */
        private double visibility;
    }
}
