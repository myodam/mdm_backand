package com.ux.mbm.mission.repository;

import com.ux.mbm.mission.entity.MissionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 미션 판정 결과 레포지토리
 *
 * 기획 문서 §15.1 기준으로 저장합니다.
 * 좌표 원본은 저장하지 않습니다.
 */
public interface MissionResultRepository extends JpaRepository<MissionResult, Long> {

    /** storyId + sceneId 기준 결과 목록 조회 */
    List<MissionResult> findAllByStoryIdAndSceneId(String storyId, String sceneId);

    /** storyId 기준 전체 결과 조회 */
    List<MissionResult> findAllByStoryId(String storyId);
}
