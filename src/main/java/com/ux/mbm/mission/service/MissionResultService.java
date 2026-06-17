package com.ux.mbm.mission.service;

import com.ux.mbm.global.ai.AiServerClient;
import com.ux.mbm.global.ai.AiServerRequest;
import com.ux.mbm.global.ai.AiServerResponse;
import com.ux.mbm.global.code.ErrorCode;
import com.ux.mbm.global.code.MessageMapper;
import com.ux.mbm.global.code.ReasonCode;
import com.ux.mbm.mission.dto.MissionResultRequest;
import com.ux.mbm.mission.dto.MissionResultResponse;
import com.ux.mbm.mission.entity.MissionResult;
import com.ux.mbm.mission.repository.MissionResultRepository;
import com.ux.mbm.story.entity.StoryScene;
import com.ux.mbm.story.service.StorySceneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 미션 판정 서비스
 *
 * 기획 문서 처리 순서:
 * [1] storyId + sceneId + missionType 유효성 검사 → MISSION_MISMATCH
 * [2] AI 서버 호출 → AI_SERVER_ERROR
 * [3] AI 응답 파싱
 *     - errorCode 있음 → 판정 불가 (USER_NOT_DETECTED, HAND_NOT_VISIBLE 등)
 *     - success=true  → 미션 성공 → nextSceneId/NEXT_SCENE or ENDING
 *     - success=false → 미션 실패 → RETRY
 * [4] DB 저장 (실패 시 SAVE_FAILED 경고, 응답은 정상 반환)
 * [5] Unity에 최종 응답 반환
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MissionResultService {

    private final MissionResultRepository missionResultRepository;
    private final AiServerClient aiServerClient;
    private final StorySceneService storySceneService;

    /**
     * POST /api/missions/check
     * Unity가 수집한 poseFrames를 받아 AI 판정 후 결과를 반환합니다.
     *
     * @Transactional(NOT_SUPPORTED): AI 서버 HTTP 호출이 긴 트랜잭션을 점유하지 않도록
     * 전체 메서드는 트랜잭션 없이 실행하고 DB 저장만 별도 트랜잭션으로 처리합니다.
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public MissionResultResponse judge(MissionResultRequest request) {

        // ── [1] storyId + sceneId + missionType 유효성 검사 ──────────
        Optional<StoryScene> sceneOpt =
                storySceneService.findScene(request.getStoryId(), request.getSceneId());

        if (sceneOpt.isEmpty()) {
            log.warn("[MISSION_MISMATCH] 지원하지 않는 storyId 또는 sceneId - storyId={}, sceneId={}",
                    request.getStoryId(), request.getSceneId());
            return buildErrorResponse(request.getSceneId(), ErrorCode.MISSION_MISMATCH);
        }

        StoryScene scene = sceneOpt.get();

        if (!scene.getMissionType().equals(request.getMissionType())) {
            log.warn("[MISSION_MISMATCH] missionType 불일치 - sceneId={}, expected={}, actual={}",
                    request.getSceneId(), scene.getMissionType(), request.getMissionType());
            return buildErrorResponse(request.getSceneId(), ErrorCode.MISSION_MISMATCH);
        }

        // ── [2] AI 서버 호출 ─────────────────────────────────────────
        // POST /internal/ai/missions/check
        AiServerResponse aiResponse = aiServerClient.requestJudge(AiServerRequest.from(request));

        if (aiResponse == null) {
            log.error("[AI_SERVER_ERROR] AI 서버 응답 없음 - sceneId={}, missionType={}",
                    request.getSceneId(), request.getMissionType());
            return buildErrorResponse(request.getSceneId(), ErrorCode.AI_SERVER_ERROR);
        }

        // ── [3] AI 응답 파싱 ─────────────────────────────────────────
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
            // AI 서버가 errorCode 반환 → 판정 불가 (USER_NOT_DETECTED, HAND_NOT_VISIBLE 등)
            success      = false;
            sceneCleared = false;
            message      = MessageMapper.fromErrorCode(aiErrorCode);
            nextAction   = "RETRY";
            nextSceneId  = null;
            log.info("[AI_ERROR] sceneId={}, errorCode={}", request.getSceneId(), aiErrorCode);

        } else if (aiSuccess) {
            // 미션 성공 — nextSceneId null이면 ENDING, 있으면 NEXT_SCENE
            success      = true;
            sceneCleared = true;
            message      = MessageMapper.fromReasonCode(
                    aiReasonCode != null ? aiReasonCode : ReasonCode.MISSION_SUCCESS.name(),
                    request.getStoryId(), request.getSceneId());
            nextSceneId  = scene.getNextSceneId();
            nextAction   = (nextSceneId != null) ? "NEXT_SCENE" : "ENDING";
            log.info("[MISSION_SUCCESS] sceneId={}, score={}, nextAction={}, nextSceneId={}",
                    request.getSceneId(), score, nextAction, nextSceneId);

        } else {
            // 미션 실패 (HANDS_TOO_FAR, HAND_NOT_RAISED, ARMS_NOT_WIDE 등)
            success      = false;
            sceneCleared = false;
            message      = MessageMapper.fromReasonCode(
                    aiReasonCode, request.getStoryId(), request.getSceneId());
            nextAction   = "RETRY";
            nextSceneId  = null;
            log.info("[MISSION_FAIL] sceneId={}, score={}, reasonCode={}",
                    request.getSceneId(), score, aiReasonCode);
        }

        // ── [4] DB 저장 ──────────────────────────────────────────────
        // 저장 실패 시 SAVE_FAILED 경고, 응답은 정상 반환
        String warningCode = saveResultSafely(
                request, success, score, aiReasonCode, aiErrorCode, message);

        // ── [5] Unity에 최종 응답 반환 ───────────────────────────────
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

    // ── private 헬퍼 ─────────────────────────────────────────────────

    /** errorCode 기반 실패 응답 생성 */
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

    /**
     * DB 저장 (별도 트랜잭션)
     * AI 판정 성공 후 DB 저장 실패 시 SAVE_FAILED 반환, 사용자 진행은 막지 않습니다.
     *
     * @return warningCode ("SAVE_FAILED" 또는 null)
     */
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
            log.debug("[SAVE_SUCCESS] storyId={}, sceneId={}", request.getStoryId(), request.getSceneId());
            return null;

        } catch (Exception e) {
            log.error("[SAVE_FAILED] DB 저장 실패 - storyId={}, sceneId={}, error={}",
                    request.getStoryId(), request.getSceneId(), e.getMessage());
            return "SAVE_FAILED";
        }
    }
}
