package com.ux.mbm.mission.service;

import com.ux.mbm.global.ai.AiServerClient;
import com.ux.mbm.global.ai.AiServerRequest;
import com.ux.mbm.global.ai.AiServerResponse;
import com.ux.mbm.global.code.ErrorCode;
import com.ux.mbm.global.code.ReasonCode;
import com.ux.mbm.mission.dto.MissionResultRequest;
import com.ux.mbm.mission.dto.MissionResultResponse;
import com.ux.mbm.mission.entity.MissionResult;
import com.ux.mbm.mission.repository.MissionResultRepository;
import com.ux.mbm.story.entity.StoryScene;
import com.ux.mbm.story.service.SceneMessageResolver;
import com.ux.mbm.story.service.StorySceneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionResultService {

    private final MissionResultRepository missionResultRepository;
    private final AiServerClient aiServerClient;
    private final StorySceneService storySceneService;
    private final SceneMessageResolver messageResolver;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public MissionResultResponse judge(MissionResultRequest request) {

        // ── [1] storyId + sceneId 유효성 검사 ────────────────────────
        Optional<StoryScene> sceneOpt =
                storySceneService.findScene(request.getStoryId(), request.getSceneId());

        if (sceneOpt.isEmpty()) {
            log.warn("[MISSION_MISMATCH] 지원하지 않는 storyId/sceneId - storyId={}, sceneId={}",
                    request.getStoryId(), request.getSceneId());
            return buildErrorResponse(request.getSceneId(), ErrorCode.MISSION_MISMATCH);
        }

        StoryScene scene = sceneOpt.get();

        // ── [2] beforeMessage=true → AI 호출 없이 before_message 즉시 반환 ──
        if (request.isBeforeMission()) {
            String msg = scene.getBeforeMessage() != null
                    ? scene.getBeforeMessage()
                    : "미션을 시작해보세요!";

            log.info("[BEFORE_MESSAGE] sceneId={}, message={}", request.getSceneId(), msg);

            return MissionResultResponse.builder()
                    .success(false)
                    .sceneCleared(false)
                    .currentSceneId(request.getSceneId())
                    .nextSceneId(null)
                    .nextAction("READY")          // Unity: 미션 시작 대기 상태
                    .score(0.0)
                    .reasonCode(null)
                    .message(msg)
                    .errorCode(null)
                    .warningCode(null)
                    .build();
        }

        // ── [3] missionType 검증 (AI 호출 전) ────────────────────────
        if (!scene.getMissionType().equals(request.getMissionType())) {
            log.warn("[UNKNOWN_MISSION_TYPE] missionType 불일치 - sceneId={}, expected={}, actual={}",
                    request.getSceneId(), scene.getMissionType(), request.getMissionType());
            return buildErrorResponse(request.getSceneId(), ErrorCode.UNKNOWN_MISSION_TYPE);
        }

        // ── [4] AI 서버 호출 ──────────────────────────────────────────
        // storyId, sceneId, beforeMessage 제외하고 전달
        AiServerResponse aiResponse = aiServerClient.requestJudge(AiServerRequest.from(request));

        if (aiResponse == null) {
            log.error("[AI_SERVER_ERROR] AI 서버 응답 없음 - sceneId={}", request.getSceneId());
            return buildErrorResponse(request.getSceneId(), ErrorCode.AI_SERVER_ERROR);
        }

        // ── [5] AI 응답 파싱 ──────────────────────────────────────────
        String  aiErrorCode  = aiResponse.getErrorCode();
        String  aiReasonCode = aiResponse.getReasonCode();
        boolean aiSuccess    = aiResponse.isSuccess();
        double  score        = aiResponse.getScore();

        boolean success;
        boolean sceneCleared;
        String  message;
        String  nextAction;
        String  nextSceneId;

        if (aiErrorCode != null) {
            success      = false;
            sceneCleared = false;
            message      = messageResolver.resolveErrorMessage(scene, aiErrorCode);
            nextAction   = "RETRY";
            nextSceneId  = null;
            log.info("[AI_ERROR] sceneId={}, errorCode={}", request.getSceneId(), aiErrorCode);

        } else if (aiSuccess) {
            success      = true;
            sceneCleared = true;
            message      = messageResolver.resolveSuccessMessage(scene);
            nextSceneId  = scene.getNextSceneId();
            nextAction   = (nextSceneId != null) ? "NEXT_SCENE" : "ENDING";
            log.info("[MISSION_SUCCESS] sceneId={}, score={}, nextAction={}", request.getSceneId(), score, nextAction);

        } else {
            String resolvedReasonCode = aiReasonCode != null ? aiReasonCode : ReasonCode.LOW_SCORE.name();
            success      = false;
            sceneCleared = false;
            message      = messageResolver.resolveFailMessage(scene, resolvedReasonCode);
            nextAction   = "RETRY";
            nextSceneId  = null;
            log.info("[MISSION_FAIL] sceneId={}, score={}, reasonCode={}", request.getSceneId(), score, aiReasonCode);
        }

        // ── [6] DB 저장 ───────────────────────────────────────────────
        String warningCode = saveResultSafely(request, success, score, aiReasonCode, aiErrorCode, message);

        // ── [7] Unity 최종 응답 ───────────────────────────────────────
        return MissionResultResponse.builder()
                .success(success)
                .sceneCleared(sceneCleared)
                .currentSceneId(request.getSceneId())
                .nextSceneId(nextSceneId)
                .nextAction(nextAction)
                .score(score)
                .reasonCode(aiReasonCode)
                .message(message)
                .errorCode(aiErrorCode)
                .warningCode(warningCode)
                .build();
    }

    private MissionResultResponse buildErrorResponse(String sceneId, ErrorCode errorCode) {
        return MissionResultResponse.builder()
                .success(false)
                .sceneCleared(false)
                .currentSceneId(sceneId)
                .nextSceneId(null)
                .nextAction("RETRY")
                .score(0.0)
                .reasonCode(null)
                .message(errorCode.getMessage())
                .errorCode(errorCode.name())
                .warningCode(null)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String saveResultSafely(MissionResultRequest request,
                                   boolean success, double score,
                                   String reasonCode, String errorCode,
                                   String message) {
        try {
            missionResultRepository.save(
                    MissionResult.builder()
                            .storyId(request.getStoryId())
                            .sceneId(request.getSceneId())
                            .missionType(request.getMissionType())
                            .success(success)
                            .score(score)
                            .reasonCode(reasonCode)
                            .message(message)
                            .errorCode(errorCode)
                            .warningCode(null)
                            .build()
            );
            return null;
        } catch (Exception e) {
            log.error("[SAVE_FAILED] storyId={}, sceneId={}, error={}",
                    request.getStoryId(), request.getSceneId(), e.getMessage());
            return "SAVE_FAILED";
        }
    }
}