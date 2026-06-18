package com.ux.mbm.global.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Backend → AI Server 호출
 *
 * API: POST /internal/ai/missions/check
 * 전달 필드: missionType, captureDurationSec, sampleFps, poseFrames
 * 제외 필드: storyId, sceneId, beforeMessage (AI 학습/판정에 불필요)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiServerClient {

    private final RestClient restClient;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public AiServerResponse requestJudge(AiServerRequest request) {
        try {
            AiServerResponse response = restClient.post()
                    .uri(aiServerUrl + "/internal/ai/missions/check")
                    .body(request)
                    .retrieve()
                    .body(AiServerResponse.class);

            if (response == null) {
                log.error("[AI_SERVER] 응답 null - missionType={}", request.getMissionType());
                return null;
            }

            log.info("[AI_SERVER] 판정 완료 - missionType={}, success={}, score={}, reasonCode={}, errorCode={}",
                    request.getMissionType(),
                    response.isSuccess(), response.getScore(),
                    response.getReasonCode(), response.getErrorCode());

            return response;

        } catch (RestClientException e) {
            log.error("[AI_SERVER] 호출 실패 - missionType={}, error={}",
                    request.getMissionType(), e.getMessage());
            return null;
        }
    }
}