// ========================================
// DummyDataController.java (API로 실행)
// ========================================
package org.sparta.streaming.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dummy")
@RequiredArgsConstructor
public class DummyDataController {

    private final DummyDataGenerator generator;

    /**
     * 더미 데이터 생성 API
     * POST /api/dummy/generate
     *
     * ⚠️ 주의: 약 5-10분 소요됩니다!
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateDummyData() {
        try {
            generator.generateAllDummyData();
            return ResponseEntity.ok("더미 데이터 생성 완료! 100만 건 이상 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("더미 데이터 생성 실패: " + e.getMessage());
        }
    }
}