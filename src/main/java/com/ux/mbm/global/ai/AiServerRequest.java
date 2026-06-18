package com.ux.mbm.global.ai;

import com.ux.mbm.mission.dto.MissionResultRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Backend → AI Server 요청 바디
 *
 * 기획 §2: AI에게 넘길 때 storyId, sceneId, beforeMessage 제외
 * AI 서버는 missionType + poseFrames 기반으로 판정만 수행
 */
@Getter
@Builder
public class AiServerRequest {

    // storyId, sceneId, beforeMessage 제외 (AI 학습/판정에 불필요)
    private String missionType;
    private int captureDurationSec;
    private int sampleFps;
    private List<MissionResultRequest.PoseFrame> poseFrames;

    public static AiServerRequest from(MissionResultRequest req) {
        return AiServerRequest.builder()
                .missionType(req.getMissionType())
                .captureDurationSec(req.getCaptureDurationSec())
                .sampleFps(req.getSampleFps())
                .poseFrames(req.getPoseFrames())
                .build();
    }
}