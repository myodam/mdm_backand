package com.ux.mbm.global.ai;

import com.ux.mbm.mission.dto.MissionResultRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 기획 문서 §9.1: Backend → AI Server 요청 바디
 *
 * 백엔드가 AI 서버에 전달하는 데이터입니다.
 * Unity 요청과 거의 동일하나, AI 판정에 필요한 값만 전달합니다.
 * attemptCount 없음.
 */
@Getter
@Builder
public class AiServerRequest {

    private String storyId;
    private String sceneId;
    private String missionType;
    private int captureDurationSec;
    private int sampleFps;
    private List<MissionResultRequest.PoseFrame> poseFrames;

    /**
     * Unity 요청 DTO로부터 AI 서버 요청 DTO 생성
     */
    public static AiServerRequest from(MissionResultRequest req) {
        return AiServerRequest.builder()
                .storyId(req.getStoryId())
                .sceneId(req.getSceneId())
                .missionType(req.getMissionType())
                .captureDurationSec(req.getCaptureDurationSec())
                .sampleFps(req.getSampleFps())
                .poseFrames(req.getPoseFrames())
                .build();
    }
}
