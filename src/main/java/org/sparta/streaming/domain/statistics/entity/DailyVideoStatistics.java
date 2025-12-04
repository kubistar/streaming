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
@Table(name = "daily_video_statistics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "stat_date"}),
        indexes = {
                @Index(name = "idx_stat_date", columnList = "stat_date"),
                @Index(name = "idx_view_count", columnList = "view_count DESC"),
                @Index(name = "idx_watch_time", columnList = "total_watch_seconds DESC")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyVideoStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "total_watch_seconds", nullable = false)
    private Long totalWatchSeconds = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private DailyVideoStatistics(Video video, LocalDate statDate,
                                 Integer viewCount, Long totalWatchSeconds) {
        this.video = video;
        this.statDate = statDate;
        this.viewCount = viewCount;
        this.totalWatchSeconds = totalWatchSeconds;
    }

    public static DailyVideoStatistics create(Video video, LocalDate statDate,
                                              Integer viewCount, Long totalWatchSeconds) {
        return DailyVideoStatistics.builder()
                .video(video)
                .statDate(statDate)
                .viewCount(viewCount)
                .totalWatchSeconds(totalWatchSeconds)
                .build();
    }
}
