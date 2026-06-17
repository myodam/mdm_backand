package com.ux.mbm.mission.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 미션 판정 결과 저장 엔티티
 *
 * 기획 문서 §15.1 저장 항목:
 * - id, story_id, scene_id, mission_type
 * - success, score, reason_code, message, error_code, warning_code
 * - created_at
 *
 * 제외 항목:
 * - userId (로그인 미구현)
 * - attemptCount (기획 제외)
 * - 좌표 원본 (미저장)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "mission_results",
        indexes = @Index(name = "idx_mission_scene", columnList = "story_id, scene_id")
)
public class MissionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "story_id", nullable = false, length = 50)
    private String storyId;

    @Column(name = "scene_id", nullable = false, length = 50)
    private String sceneId;

    @Column(name = "mission_type", nullable = false, length = 50)
    private String missionType;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false)
    private double score;

    @Column(name = "reason_code", length = 50)
    private String reasonCode;

    @Column(length = 255)
    private String message;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "warning_code", length = 50)
    private String warningCode;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MissionResult(String storyId, String sceneId, String missionType,
                         boolean success, double score,
                         String reasonCode, String message,
                         String errorCode, String warningCode) {
        this.storyId = storyId;
        this.sceneId = sceneId;
        this.missionType = missionType;
        this.success = success;
        this.score = score;
        this.reasonCode = reasonCode;
        this.message = message;
        this.errorCode = errorCode;
        this.warningCode = warningCode;
    }
}
