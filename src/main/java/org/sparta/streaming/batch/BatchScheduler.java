// ========================================
// BatchScheduler.java (배치 자동 실행)
// ========================================
package org.sparta.streaming.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Profile("!test")  // 테스트 환경에서는 자동 실행 안 함
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job dailyStatisticsJob;
    private final Job dailySettlementJob;

    /**
     * 매일 새벽 1시에 통계 배치 실행
     */
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void runDailyStatistics() {
        try {
            log.info("=== 일간 통계 배치 시작 ===");

            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("timestamp", LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(dailyStatisticsJob, params);

            log.info("=== 일간 통계 배치 완료 ===");
        } catch (Exception e) {
            log.error("통계 배치 실행 중 오류 발생", e);
        }
    }

    /**
     * 매일 새벽 2시에 정산 배치 실행 (통계 배치 이후)
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void runDailySettlement() {
        try {
            log.info("=== 일간 정산 배치 시작 ===");

            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("timestamp", LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(dailySettlementJob, params);

            log.info("=== 일간 정산 배치 완료 ===");
        } catch (Exception e) {
            log.error("정산 배치 실행 중 오류 발생", e);
        }
    }
}