package com.ux.mbm.story.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 동화별 씬/미션 매핑 테이블
 *
 * V3 변경사항:
 * - before_message: 미션 시작 전 사용자에게 보여줄 안내 메시지
 *   (beforeMessage=true 요청 시 반환, AI 호출 없음)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "story_scenes",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_story_scene",
                columnNames = {"story_id", "scene_id"}
        )
)
public class StoryScene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "story_id", nullable = false, length = 50)
    private String storyId;

    @Column(name = "scene_id", nullable = false, length = 50)
    private String sceneId;

    @Column(name = "mission_type", nullable = false, length = 50)
    private String missionType;

    /** 성공 시 다음 씬 ID. null = 마지막 씬 → ENDING */
    @Column(name = "next_scene_id", length = 50)
    private String nextSceneId;

    @Column(name = "scene_order", nullable = false)
    private int sceneOrder;

    /** MISSION_SUCCESS 시 사용자 메시지. DB 관리 → 대사 변경 시 코드 배포 불필요 */
    @Column(name = "success_message", length = 255)
    private String successMessage;

    /** reasonCode → 메시지 매핑 JSON. DB 관리 */
    @Column(name = "fail_messages", columnDefinition = "TEXT")
    private String failMessages;

    /**
     * 미션 시작 전 안내 메시지 (beforeMessage=true 요청 시 반환)
     * 예: "두 손을 모아서 제비를 보호해보세요!"
     * AI 호출 없이 즉시 반환되므로 빠른 UX 제공 가능
     */
    @Column(name = "before_message", length = 255)
    private String beforeMessage;

    @Builder
    public StoryScene(String storyId, String sceneId, String missionType,
                      String nextSceneId, int sceneOrder,
                      String successMessage, String failMessages, String beforeMessage) {
        this.storyId = storyId;
        this.sceneId = sceneId;
        this.missionType = missionType;
        this.nextSceneId = nextSceneId;
        this.sceneOrder = sceneOrder;
        this.successMessage = successMessage;
        this.failMessages = failMessages;
        this.beforeMessage = beforeMessage;
    }
}