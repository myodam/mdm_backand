package com.ux.mbm.global.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 기획 문서 §17.1: Backend Health Check
 * GET /api/health → { "status": "ok", "service": "backend" }
 */
@Tag(name = "Health", description = "서버 상태 확인")
@RestController
@RequestMapping("/api")
public class HealthController {

    @Operation(summary = "Backend Health Check", description = "백엔드 서버 상태를 확인합니다.")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "service", "backend"
        ));
    }
}
