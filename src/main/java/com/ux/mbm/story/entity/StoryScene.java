package com.ux.mbm.story.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 동화별 씬/미션 매핑 테이블
 *
 * 기획 문서 §14: storyId / sceneId / missionType 매핑
 *
 * MVP 데이터:
 * | storyId         | sceneId   | missionType      | nextSceneId | sceneOrder |
 * |-----------------|-----------|------------------|-------------|------------|
 * | heungbu_nolbu   | scene_001 | protect_swallow  | scene_002   | 1          |
 * | heungbu_nolbu   | scene_002 | receive_seed     | scene_003   | 2          |
 * | heungbu_nolbu   | scene_003 | open_gourd       | null        | 3          |
 *
 * nextSceneId가 null이면 마지막 씬 → ENDING
 *
 * 새 동화 추가 시 DB INSERT만 하면 됨 (코드 배포 불필요)
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

    /** 동화 ID (예: heungbu_nolbu) */
    @Column(name = "story_id", nullable = false, length = 50)
    private String storyId;

    /** 씬 ID (예: scene_001) */
    @Column(name = "scene_id", nullable = false, length = 50)
    private String sceneId;

    /** 미션 타입 (예: protect_swallow) */
    @Column(name = "mission_type", nullable = false, length = 50)
    private String missionType;

    /**
     * 성공 시 이동할 다음 씬 ID
     * null이면 마지막 씬 → nextAction = ENDING
     */
    @Column(name = "next_scene_id", length = 50)
    private String nextSceneId;

    /** 씬 순서 (오름차순 정렬 기준) */
    @Column(name = "scene_order", nullable = false)
    private int sceneOrder;

    @Builder
    public StoryScene(String storyId, String sceneId, String missionType,
                      String nextSceneId, int sceneOrder) {
        this.storyId = storyId;
        this.sceneId = sceneId;
        this.missionType = missionType;
        this.nextSceneId = nextSceneId;
        this.sceneOrder = sceneOrder;
    }
}
