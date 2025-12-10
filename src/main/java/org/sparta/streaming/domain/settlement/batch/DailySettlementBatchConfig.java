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
        // CTE와 LEFT JOIN을 사용한 개선된 쿼리
        String sql = """
            WITH settlement_target AS (
                SELECT DISTINCT video_id
                FROM daily_video_statistics
                WHERE stat_date = CURDATE() - INTERVAL 1 DAY
            ),
            video_stats AS (
                SELECT 
                    video_id,
                    COALESCE(SUM(CASE WHEN stat_date < CURDATE() - INTERVAL 1 DAY THEN view_count ELSE 0 END), 0) as previous_total_views,
                    COALESCE(SUM(CASE WHEN stat_date = CURDATE() - INTERVAL 1 DAY THEN view_count ELSE 0 END), 0) as today_views
                FROM daily_video_statistics
                WHERE video_id IN (SELECT video_id FROM settlement_target)
                GROUP BY video_id
            ),
            ad_stats AS (
                SELECT 
                    va.video_id,
                    COUNT(CASE WHEN DATE(awh.created_at) < CURDATE() - INTERVAL 1 DAY THEN 1 END) as previous_total_ad_views,
                    COUNT(CASE WHEN DATE(awh.created_at) = CURDATE() - INTERVAL 1 DAY THEN 1 END) as today_ad_views
                FROM video_ads va
                INNER JOIN ad_watch_history awh ON va.video_ads_id = awh.video_ads_id
                WHERE va.video_id IN (SELECT video_id FROM settlement_target)
                  AND awh.view_counted = true
                  AND DATE(awh.created_at) <= CURDATE() - INTERVAL 1 DAY
                GROUP BY va.video_id
            )
            SELECT 
                v.video_id,
                v.user_id,
                CURDATE() - INTERVAL 1 DAY as settlement_date,
                COALESCE(vs.previous_total_views, 0) as previous_total_views,
                COALESCE(vs.today_views, 0) as today_views,
                COALESCE(ad.previous_total_ad_views, 0) as previous_total_ad_views,
                COALESCE(ad.today_ad_views, 0) as today_ad_views
            FROM videos v
            INNER JOIN settlement_target st ON v.video_id = st.video_id
            LEFT JOIN video_stats vs ON v.video_id = vs.video_id
            LEFT JOIN ad_stats ad ON v.video_id = ad.video_id
            """;

        RowMapper<SettlementSourceDto> rowMapper = (rs, rowNum) -> {
            int videoId = rs.getInt("video_id");
            int userId = rs.getInt("user_id");
            LocalDate settlementDate = rs.getDate("settlement_date").toLocalDate();
            int previousTotalViews = rs.getInt("previous_total_views");
            int todayViews = rs.getInt("today_views");
            int previousTotalAdViews = rs.getInt("previous_total_ad_views");
            int todayAdViews = rs.getInt("today_ad_views");

            log.debug("Read settlement source: videoId={}, previousAdViews={}, todayAdViews={}",
                    videoId, previousTotalAdViews, todayAdViews);

            return new SettlementSourceDto(
                    videoId, userId, settlementDate,
                    previousTotalViews, todayViews,
                    previousTotalAdViews, todayAdViews
            );
        };

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

            log.info("정산 처리: videoId={}, date={}, prevAdViews={}, todayAdViews={}, totalAdViews={}, videoAmount={}, adAmount={}, total={}",
                    source.videoId(), source.settlementDate(),
                    source.previousTotalAdViews(), source.todayAdViews(), totalAdViews,
                    videoAmount, adAmount, videoAmount.add(adAmount));

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