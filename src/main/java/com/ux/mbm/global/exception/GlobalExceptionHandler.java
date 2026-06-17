package com.ux.mbm.global.exception;

import com.ux.mbm.global.code.ErrorCode;
import com.ux.mbm.mission.dto.MissionResultRequest;
import com.ux.mbm.mission.dto.MissionResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리
 *
 * 기획 문서 §13: 예외 처리
 * - @Valid 실패 → INVALID_POSE_DATA 응답
 * - CustomException → 해당 ErrorCode 응답
 * - 그 외 예외 → INTERNAL_SERVER_ERROR 응답
 *
 * 모든 예외 응답은 MissionResultResponse 형식으로 통일합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 기획 문서 §13.4: @Valid 검증 실패 → INVALID_POSE_DATA
     * poseFrames 누락, storyId/sceneId/missionType 누락 등
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MissionResultResponse> handleValidException(
            MethodArgumentNotValidException e) {

        String fieldError = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("[INVALID_REQUEST] 요청 검증 실패: {}", fieldError);

        String sceneId = extractSceneId(e);

        return ResponseEntity.badRequest().body(
                MissionResultResponse.builder()
                        .success(false)
                        .sceneCleared(false)
                        .currentSceneId(sceneId)
                        .nextSceneId(null)
                        .nextAction("RETRY")
                        .score(0.0)
                        .reasonCode(null)
                        .message(ErrorCode.INVALID_POSE_DATA.getMessage())
                        .errorCode(ErrorCode.INVALID_POSE_DATA.name())
                        .warningCode(null)
                        .build()
        );
    }

    /**
     * 기획 문서 §7.6: CustomException → 해당 ErrorCode 응답
     * MISSION_MISMATCH, AI_SERVER_ERROR 등
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<MissionResultResponse> handleCustomException(CustomException e) {
        log.warn("[CUSTOM_EXCEPTION] errorCode={}, message={}", e.getErrorCode(), e.getMessage());

        return ResponseEntity.badRequest().body(
                MissionResultResponse.builder()
                        .success(false)
                        .sceneCleared(false)
                        .currentSceneId("unknown")
                        .nextSceneId(null)
                        .nextAction("RETRY")
                        .score(0.0)
                        .reasonCode(null)
                        .message(e.getErrorCode().getMessage())
                        .errorCode(e.getErrorCode().name())
                        .warningCode(null)
                        .build()
        );
    }

    /**
     * 기획 문서 §7.6: 그 외 예외 → INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MissionResultResponse> handleException(Exception e) {
        log.error("[INTERNAL_SERVER_ERROR] {}", e.getMessage(), e);

        return ResponseEntity.internalServerError().body(
                MissionResultResponse.builder()
                        .success(false)
                        .sceneCleared(false)
                        .currentSceneId("unknown")
                        .nextSceneId(null)
                        .nextAction("RETRY")
                        .score(0.0)
                        .reasonCode(null)
                        .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                        .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.name())
                        .warningCode(null)
                        .build()
        );
    }

    /** @Valid 실패 시 요청 객체에서 sceneId 추출 (가능한 경우) */
    private String extractSceneId(MethodArgumentNotValidException e) {
        try {
            Object target = e.getBindingResult().getTarget();
            if (target instanceof MissionResultRequest req && req.getSceneId() != null) {
                return req.getSceneId();
            }
        } catch (Exception ignored) {}
        return "unknown";
    }
}
