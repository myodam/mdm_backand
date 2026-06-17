package com.ux.mbm.global.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 기획 문서 §9.1: Backend → AI Server 호출
 *
 * Unity는 AI 서버를 직접 호출하지 않습니다.
 * 백엔드가 내부적으로 AI 서버를 호출하고 결과를 Unity에 반환합니다.
 *
 * API: POST /internal/ai/missions/check
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiServerClient {

    private final RestClient restClient;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    /**
     * AI 서버에 동작 판정을 요청합니다.
     *
     * @param request poseFrames 포함 판정 요청
     * @return AI 판정 결과, 호출 실패 시 null 반환
     */
    public AiServerResponse requestJudge(AiServerRequest request) {
        try {
            AiServerResponse response = restClient.post()
                    .uri(aiServerUrl + "/internal/ai/missions/check")
                    .body(request)
                    .retrieve()
                    .body(AiServerResponse.class);

            if (response == null) {
                log.error("[AI_SERVER] 응답 null - sceneId={}, missionType={}",
                        request.getSceneId(), request.getMissionType());
                return null;
            }

            log.info("[AI_SERVER] 판정 완료 - sceneId={}, missionType={}, success={}, score={}, reasonCode={}, errorCode={}",
                    request.getSceneId(), request.getMissionType(),
                    response.isSuccess(), response.getScore(),
                    response.getReasonCode(), response.getErrorCode());

            return response;

        } catch (RestClientException e) {
            log.error("[AI_SERVER] 호출 실패 - sceneId={}, missionType={}, error={}",
                    request.getSceneId(), request.getMissionType(), e.getMessage());
            return null;
        }
    }
}
