package com.ux.mbm.story.service;

import com.ux.mbm.story.entity.StoryScene;
import com.ux.mbm.story.repository.StorySceneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 동화 씬 서비스
 *
 * storyId + sceneId 기준으로 씬 정보를 조회합니다.
 * MissionResultService에서 MISSION_MISMATCH 검증에 사용합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorySceneService {

    private final StorySceneRepository storySceneRepository;

    /**
     * storyId + sceneId 조합으로 씬 조회
     *
     * @param storyId 동화 ID
     * @param sceneId 씬 ID
     * @return 씬 정보 (없으면 empty → MISSION_MISMATCH)
     */
    public Optional<StoryScene> findScene(String storyId, String sceneId) {
        return storySceneRepository.findByStoryIdAndSceneId(storyId, sceneId);
    }
}
