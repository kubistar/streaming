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
@Table(name = "weekly_video_statistics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "week_start_date"}),
        indexes = {
                @Index(name = "idx_week_start", columnList = "week_start_date"),
                @Index(name = "idx_weekly_view", columnList = "view_count DESC"),
                @Index(name = "idx_weekly_watch", columnList = "total_watch_seconds DESC")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyVideoStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "week_end_date", nullable = false)
    private LocalDate weekEndDate;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "total_watch_seconds", nullable = false)
    private Long totalWatchSeconds = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private WeeklyVideoStatistics(Video video, LocalDate weekStartDate, LocalDate weekEndDate,
                                  Integer viewCount, Long totalWatchSeconds) {
        this.video = video;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.viewCount = viewCount;
        this.totalWatchSeconds = totalWatchSeconds;
    }

    public static WeeklyVideoStatistics create(Video video, LocalDate weekStartDate, LocalDate weekEndDate,
                                               Integer viewCount, Long totalWatchSeconds) {
        return WeeklyVideoStatistics.builder()
                .video(video)
                .weekStartDate(weekStartDate)
                .weekEndDate(weekEndDate)
                .viewCount(viewCount)
                .totalWatchSeconds(totalWatchSeconds)
                .build();
    }
}