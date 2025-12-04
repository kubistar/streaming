// ========================================
// DailySettlementBatchConfig.java
// ========================================
package org.sparta.streaming.domain.settlement.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.streaming.domain.settlement.entity.DailyVideoSettlement;
import org.sparta.streaming.domain.settlement.util.SettlementCalculator;
import org.sparta.streaming.domain.user.entity.User;
import org.sparta.streaming.domain.user.repository.UserRepository;
import org.sparta.streaming.domain.video.entity.Video;
import org.sparta.streaming.domain.video.repository.VideoRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("batch")  // batch 프로필에서만 활성화
public class DailySettlementBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final SettlementCalculator calculator;

    /**
     * 일간 정산 생성 Job
     */
    @Bean
    public Job dailySettlementJob() {
        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(dailySettlementStep())
                .build();
    }

    /**
     * 일간 정산 Step
     */
    @Bean
    public Step dailySettlementStep() {
        return new StepBuilder("dailySettlementStep", jobRepository)
                .<SettlementSourceDto, DailyVideoSettlement>chunk(1000, transactionManager)
                .reader(settlementReader())
                .processor(settlementProcessor())
                .writer(settlementWriter())
                .build();
    }

    /**
     * 정산 데이터 읽기
     */
    @Bean
    public JdbcCursorItemReader<SettlementSourceDto> settlementReader() {
        String sql = """
            SELECT 
                v.video_id,
                v.user_id,
                CURDATE() - INTERVAL 1 DAY as settlement_date,
                
                -- 어제까지 누적 조회수
                COALESCE((
                    SELECT SUM(view_count) 
                    FROM daily_video_statistics 
                    WHERE video_id = v.video_id 
                      AND stat_date < CURDATE() - INTERVAL 1 DAY
                ), 0) as previous_total_views,
                
                -- 어제 조회수
                COALESCE((
                    SELECT view_count 
                    FROM daily_video_statistics 
                    WHERE video_id = v.video_id 
                      AND stat_date = CURDATE() - INTERVAL 1 DAY
                ), 0) as today_views,
                
                -- 어제까지 누적 광고 조회수
                COALESCE((
                    SELECT COUNT(*)
                    FROM ad_watch_history awh
                    JOIN video_ads va ON awh.video_ads_id = va.video_ads_id
                    WHERE va.video_id = v.video_id
                      AND DATE(awh.created_at) < CURDATE() - INTERVAL 1 DAY
                      AND awh.view_counted = true
                ), 0) as previous_total_ad_views,
                
                -- 어제 광고 조회수
                COALESCE((
                    SELECT COUNT(*)
                    FROM ad_watch_history awh
                    JOIN video_ads va ON awh.video_ads_id = va.video_ads_id
                    WHERE va.video_id = v.video_id
                      AND DATE(awh.created_at) = CURDATE() - INTERVAL 1 DAY
                      AND awh.view_counted = true
                ), 0) as today_ad_views
                
            FROM videos v
            WHERE EXISTS (
                SELECT 1 FROM daily_video_statistics 
                WHERE video_id = v.video_id 
                  AND stat_date = CURDATE() - INTERVAL 1 DAY
            )
            """;

        RowMapper<SettlementSourceDto> rowMapper = (rs, rowNum) -> new SettlementSourceDto(
                rs.getInt("video_id"),
                rs.getInt("user_id"),
                rs.getDate("settlement_date").toLocalDate(),
                rs.getInt("previous_total_views"),
                rs.getInt("today_views"),
                rs.getInt("previous_total_ad_views"),
                rs.getInt("today_ad_views")
        );

        return new JdbcCursorItemReaderBuilder<SettlementSourceDto>()
                .name("settlementReader")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper(rowMapper)
                .build();
    }

    /**
     * 정산 금액 계산 및 Entity 생성
     */
    @Bean
    public ItemProcessor<SettlementSourceDto, DailyVideoSettlement> settlementProcessor() {
        return source -> {
            Video video = videoRepository.findById(source.videoId())
                    .orElseThrow(() -> new IllegalArgumentException("Video not found: " + source.videoId()));

            User user = userRepository.findById(source.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + source.userId()));

            // 영상 정산 금액 계산 (누진세)
            BigDecimal videoAmount = calculator.calculateVideoAmount(
                    source.previousTotalViews(),
                    source.todayViews()
            );

            // 광고 정산 금액 계산 (누진세)
            BigDecimal adAmount = calculator.calculateAdAmount(
                    source.previousTotalAdViews(),
                    source.todayAdViews()
            );

            int totalViews = source.previousTotalViews() + source.todayViews();
            int totalAdViews = source.previousTotalAdViews() + source.todayAdViews();

            log.info("정산 생성: videoId={}, date={}, videoAmount={}, adAmount={}, total={}",
                    source.videoId(), source.settlementDate(), videoAmount, adAmount, videoAmount.add(adAmount));

            return DailyVideoSettlement.create(
                    video,
                    user,
                    source.settlementDate(),
                    totalViews,
                    source.previousTotalViews(),
                    source.todayViews(),
                    totalAdViews,
                    source.previousTotalAdViews(),
                    source.todayAdViews(),
                    videoAmount,
                    adAmount
            );
        };
    }

    /**
     * DB에 저장
     */
    @Bean
    public JpaItemWriter<DailyVideoSettlement> settlementWriter() {
        JpaItemWriter<DailyVideoSettlement> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    /**
     * 정산 소스 DTO
     */
    public record SettlementSourceDto(
            Integer videoId,
            Integer userId,
            LocalDate settlementDate,
            Integer previousTotalViews,
            Integer todayViews,
            Integer previousTotalAdViews,
            Integer todayAdViews
    ) {}
}