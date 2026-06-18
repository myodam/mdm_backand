package com.ux.mbm.mission.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Unity → Backend 요청 바디
 *
 * beforeMission=true  → AI 호출 없이 DB의 before_message 반환 (미션 시작 전 안내)
 * beforeMission=false → 기존 AI 판정 플로우 (기본값)
 */
@Getter
@NoArgsConstructor
public class MissionResultRequest {

    @NotBlank(message = "storyId는 필수입니다.")
    private String storyId;

    @NotBlank(message = "sceneId는 필수입니다.")
    private String sceneId;

    /**
     * 미션 시작 전 안내 메시지 요청 여부
     * true  → AI 호출 없이 story_scenes.before_message 반환
     * false → 기존 AI 판정 플로우 (기본값)
     */
    private boolean beforeMission = false;

    /** beforeMission=false 일 때 필수 */
    private String missionType;

    private int captureDurationSec;
    private int sampleFps;

    /**
     * beforeMission=true 이면 빈 배열이어도 됨
     * beforeMission=false 이면 필수
     */
    private List<PoseFrame> poseFrames;

    // ── 중첩 DTO ──────────────────────────────────────────

    @Getter
    @NoArgsConstructor
    public static class PoseFrame {
        private double timestamp;
        private Map<String, Landmark> landmarks;
    }

    @Getter
    @NoArgsConstructor
    public static class Landmark {
        private double x;
        private double y;
        private double visibility;
    }
}