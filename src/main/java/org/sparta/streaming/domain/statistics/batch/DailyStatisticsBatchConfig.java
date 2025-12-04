// ========================================
// DailyStatisticsBatchConfig.java
// ========================================
package org.sparta.streaming.domain.statistics.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.streaming.domain.statistics.entity.DailyVideoStatistics;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyStatisticsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final VideoRepository videoRepository;
    private final EntityManagerFactory entityManagerFactory;

    /**
     * 일간 통계 생성 Job
     */
    @Bean
    public Job dailyStatisticsJob() {
        return new JobBuilder("dailyStatisticsJob", jobRepository)
                .start(dailyStatisticsStep())
                .build();
    }

    /**
     * 일간 통계 Step
     */
    @Bean
    public Step dailyStatisticsStep() {
        return new StepBuilder("dailyStatisticsStep", jobRepository)
                .<StatisticsDto, DailyVideoStatistics>chunk(1000, transactionManager)
                .reader(statisticsReader())
                .processor(statisticsProcessor())
                .writer(statisticsWriter())
                .build();
    }

    /**
     * 시청 기록에서 통계 데이터 읽기
     */
    @Bean
    public JdbcCursorItemReader<StatisticsDto> statisticsReader() {
        String sql = """
            SELECT 
                v.video_id,
                DATE(vh.created_at) as stat_date,
                COUNT(DISTINCT vh.record_id) as view_count,
                COALESCE(SUM(vh.total_watched_seconds), 0) as total_watch_seconds
            FROM video_watch_history vh
            JOIN videos v ON vh.video_id = v.video_id
            WHERE DATE(vh.created_at) = CURDATE() - INTERVAL 1 DAY
              AND vh.view_counted = true
            GROUP BY v.video_id, DATE(vh.created_at)
            """;

        RowMapper<StatisticsDto> rowMapper = (rs, rowNum) -> new StatisticsDto(
                rs.getInt("video_id"),
                rs.getDate("stat_date").toLocalDate(),
                rs.getInt("view_count"),
                rs.getLong("total_watch_seconds")
        );

        return new JdbcCursorItemReaderBuilder<StatisticsDto>()
                .name("statisticsReader")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper(rowMapper)
                .build();
    }

    /**
     * DTO → Entity 변환
     */
    @Bean
    public ItemProcessor<StatisticsDto, DailyVideoStatistics> statisticsProcessor() {
        return item -> {
            Video video = videoRepository.findById(item.videoId())
                    .orElseThrow(() -> new IllegalArgumentException("Video not found: " + item.videoId()));

            log.info("통계 생성: videoId={}, date={}, views={}, watchTime={}",
                    item.videoId(), item.statDate(), item.viewCount(), item.totalWatchSeconds());

            return DailyVideoStatistics.create(
                    video,
                    item.statDate(),
                    item.viewCount(),
                    item.totalWatchSeconds()
            );
        };
    }

    /**
     * DB에 저장
     */
    @Bean
    public JpaItemWriter<DailyVideoStatistics> statisticsWriter() {
        JpaItemWriter<DailyVideoStatistics> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    /**
     * 통계 DTO
     */
    public record StatisticsDto(
            Integer videoId,
            LocalDate statDate,
            Integer viewCount,
            Long totalWatchSeconds
    ) {}
}