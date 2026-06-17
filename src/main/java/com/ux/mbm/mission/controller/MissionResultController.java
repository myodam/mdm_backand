package com.ux.mbm.mission.controller;

import com.ux.mbm.mission.dto.MissionResultRequest;
import com.ux.mbm.mission.dto.MissionResultResponse;
import com.ux.mbm.mission.service.MissionResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 기획 문서 §6.1: Unity → Backend 동작 판정 요청 API
 *
 * Unity는 이 API만 호출합니다.
 * POST /api/missions/check
 */
@Tag(name = "Mission", description = "미션 판정 API - Unity가 수집한 poseFrames를 전송하면 AI 판정 후 결과를 반환합니다.")
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionResultController {

    private final MissionResultService missionResultService;

    /**
     * 동작 미션 판정
     *
     * 처리 흐름:
     * 1. @Valid로 필수 필드 검증 (storyId, sceneId, missionType, poseFrames)
     * 2. storyId + sceneId + missionType 매핑 검증
     * 3. AI 서버에 poseFrames 전달하여 동작 판정 요청
     * 4. AI 결과 기반 message, nextAction, nextSceneId 생성
     * 5. DB 저장 후 Unity에 최종 응답 반환
     */
    @Operation(
            summary = "동작 미션 판정",
            description = """
                    Unity가 MediaPipe로 수집한 poseFrames를 전송하면
                    AI 서버가 동작을 판정하고 백엔드가 최종 결과를 반환합니다.
                    
                    - 성공: nextAction=NEXT_SCENE 또는 ENDING
                    - 실패: nextAction=RETRY, 힌트 메시지 반환
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "판정 완료 (성공/실패 모두 200 반환)",
                    content = @Content(schema = @Schema(implementation = MissionResultResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 오류 (INVALID_POSE_DATA)")
    })
    @PostMapping("/check")
    public ResponseEntity<MissionResultResponse> check(
            @Valid @RequestBody MissionResultRequest request) {

        MissionResultResponse response = missionResultService.judge(request);
        return ResponseEntity.ok(response);
    }
}
