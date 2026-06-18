package com.ux.mbm.mission.repository;

import com.ux.mbm.mission.entity.MissionResult;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 미션 판정 결과 레포지토리 (로그 저장 전용)
 *
 * 기획 문서 §15.1 기준으로 저장합니다.
 * 좌표 원본은 저장하지 않습니다.
 */
public interface MissionResultRepository extends JpaRepository<MissionResult, Long> {
}
