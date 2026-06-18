package com.ux.mbm.story.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ux.mbm.global.code.ErrorCode;
import com.ux.mbm.story.entity.StoryScene;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * DB 기반 메시지 조회 컴포넌트
 *
 * 기존 MessageMapper(하드코딩 Map) 를 대체합니다.
 * story_scenes 테이블의 success_message / fail_messages 컬럼에서 메시지를 가져옵니다.
 *
 * 대사 변경 = DB UPDATE만 하면 됨. 코드 배포 불필요.
 *
 * fallback 우선순위:
 * 1. DB (story_scenes.success_message / fail_messages JSON)
 * 2. ReasonCode enum 하드코딩 메시지 (DB null인 경우 안전망)
 * 3. 기본 문자열 ("다시 시도해주세요.")
 */
@Slf4j
@Component
public class SceneMessageResolver {

    // ObjectMapper를 빈 주입 대신 직접 생성
    // (spring-boot-starter-web이 없는 환경에서도 동작)
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * MISSION_SUCCESS 시 메시지 조회
     *
     * @param scene DB에서 조회한 StoryScene
     * @return 사용자에게 보여줄 성공 메시지
     */
    public String resolveSuccessMessage(StoryScene scene) {
        // 1. DB 우선
        if (scene.getSuccessMessage() != null && !scene.getSuccessMessage().isBlank()) {
            return scene.getSuccessMessage();
        }
        // 2. fallback
        log.warn("[MESSAGE_FALLBACK] success_message가 DB에 없음 - storyId={}, sceneId={}",
                scene.getStoryId(), scene.getSceneId());
        return "잘했어요!";
    }

    /**
     * 실패 reasonCode → 메시지 조회
     *
     * @param scene      DB에서 조회한 StoryScene
     * @param reasonCode AI 서버가 반환한 reasonCode
     * @return 사용자에게 보여줄 실패 힌트 메시지
     */
    public String resolveFailMessage(StoryScene scene, String reasonCode) {
        if (reasonCode == null) {
            return "다시 시도해주세요.";
        }

        // 1. DB fail_messages JSON에서 조회
        Map<String, String> failMap = parseFailMessages(scene);
        if (!failMap.isEmpty()) {
            String msg = failMap.get(reasonCode);
            if (msg != null && !msg.isBlank()) {
                return msg;
            }
        }

        log.warn("[MESSAGE_FALLBACK] fail_messages에 reasonCode 없음 - sceneId={}, reasonCode={}",
                scene.getSceneId(), reasonCode);
        return "다시 시도해주세요.";
    }

    /**
     * errorCode → 메시지 조회
     * errorCode는 씬에 관계없이 고정 메시지이므로 ErrorCode enum 사용.
     * 단, DB fail_messages에 있으면 DB 우선 적용.
     *
     * @param scene     DB에서 조회한 StoryScene (null 허용 — 검증 실패 전 단계)
     * @param errorCode 에러 코드
     */
    public String resolveErrorMessage(StoryScene scene, String errorCode) {
        if (errorCode == null) {
            return ErrorCode.INTERNAL_SERVER_ERROR.getMessage();
        }

        // 씬이 있으면 DB fail_messages에서 먼저 확인
        if (scene != null) {
            Map<String, String> failMap = parseFailMessages(scene);
            String msg = failMap.get(errorCode);
            if (msg != null && !msg.isBlank()) {
                return msg;
            }
        }

        // ErrorCode enum fallback
        try {
            return ErrorCode.valueOf(errorCode).getMessage();
        } catch (IllegalArgumentException e) {
            return ErrorCode.INTERNAL_SERVER_ERROR.getMessage();
        }
    }

    /**
     * scene.failMessages JSON 파싱
     * 파싱 실패 시 빈 Map 반환 (서비스 흐름 중단 없음)
     */
    private Map<String, String> parseFailMessages(StoryScene scene) {
        if (scene == null || scene.getFailMessages() == null || scene.getFailMessages().isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(
                    scene.getFailMessages(),
                    new TypeReference<Map<String, String>>() {}
            );
        } catch (Exception e) {
            log.error("[MESSAGE_PARSE_ERROR] fail_messages JSON 파싱 실패 - sceneId={}, error={}",
                    scene.getSceneId(), e.getMessage());
            return Collections.emptyMap();
        }
    }
}