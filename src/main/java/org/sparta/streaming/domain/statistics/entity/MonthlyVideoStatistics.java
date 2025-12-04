package org.sparta.streaming.domain.statistics.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.sparta.streaming.domain.video.entity.Video;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_video_statistics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "year", "month"}),
        indexes = {
                @Index(name = "idx_year_month", columnList = "year, month"),
                @Index(name = "idx_monthly_view", columnList = "view_count DESC"),
                @Index(name = "idx_monthly_watch", columnList = "total_watch_seconds DESC")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyVideoStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "month_start_date", nullable = false)
    private LocalDate monthStartDate;

    @Column(name = "month_end_date", nullable = false)
    private LocalDate monthEndDate;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "total_watch_seconds", nullable = false)
    private Long totalWatchSeconds = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private MonthlyVideoStatistics(Video video, Integer year, Integer month,
                                   LocalDate monthStartDate, LocalDate monthEndDate,
                                   Integer viewCount, Long totalWatchSeconds) {
        this.video = video;
        this.year = year;
        this.month = month;
        this.monthStartDate = monthStartDate;
        this.monthEndDate = monthEndDate;
        this.viewCount = viewCount;
        this.totalWatchSeconds = totalWatchSeconds;
    }

    public static MonthlyVideoStatistics create(Video video, Integer year, Integer month,
                                                LocalDate monthStartDate, LocalDate monthEndDate,
                                                Integer viewCount, Long totalWatchSeconds) {
        return MonthlyVideoStatistics.builder()
                .video(video)
                .year(year)
                .month(month)
                .monthStartDate(monthStartDate)
                .monthEndDate(monthEndDate)
                .viewCount(viewCount)
                .totalWatchSeconds(totalWatchSeconds)
                .build();
    }
}
