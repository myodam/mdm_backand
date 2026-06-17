package com.ux.mbm.story.repository;

import com.ux.mbm.story.entity.StoryScene;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 동화별 씬/미션 매핑 레포지토리
 *
 * storyId + sceneId 조합으로 조회하여 missionType을 검증합니다.
 */
public interface StorySceneRepository extends JpaRepository<StoryScene, Long> {

    /**
     * storyId + sceneId 조합으로 씬 조회
     * MISSION_MISMATCH 검증에 사용
     */
    Optional<StoryScene> findByStoryIdAndSceneId(String storyId, String sceneId);
}
