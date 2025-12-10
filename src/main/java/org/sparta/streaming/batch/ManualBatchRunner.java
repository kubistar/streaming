// ========================================
// ManualBatchRunner.java (수동 배치 실행용)
// ========================================
package org.sparta.streaming.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Profile("batch")
public class ManualBatchRunner {

    private final JobLauncher jobLauncher;
    private final Job dailyStatisticsJob;
    private final Job dailySettlementJob;

    /**
     * 통계 배치 수동 실행
     * POST /api/batch/statistics
     */
    @PostMapping("/statistics")
    public ResponseEntity<String> runStatisticsBatch() {
        try {
            log.info("=== 통계 배치 수동 실행 시작 ===");

            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("timestamp", LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(dailyStatisticsJob, params);

            log.info("=== 통계 배치 수동 실행 완료 ===");
            return ResponseEntity.ok("통계 배치가 성공적으로 실행되었습니다.");
        } catch (Exception e) {
            log.error("통계 배치 실행 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("배치 실행 실패: " + e.getMessage());
        }
    }

    /**
     * 정산 배치 수동 실행
     * POST /api/batch/settlement
     */
    @PostMapping("/settlement")
    public ResponseEntity<String> runSettlementBatch() {
        try {
            log.info("=== 정산 배치 수동 실행 시작 ===");

            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("timestamp", LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(dailySettlementJob, params);

            log.info("=== 정산 배치 수동 실행 완료 ===");
            return ResponseEntity.ok("정산 배치가 성공적으로 실행되었습니다.");
        } catch (Exception e) {
            log.error("정산 배치 실행 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("배치 실행 실패: " + e.getMessage());
        }
    }
}
