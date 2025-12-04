// ========================================
// VideoWatchHistory.java (시청 기록)
// ========================================
package org.sparta.streaming.domain.video.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.sparta.streaming.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_watch_history",
        indexes = {
                @Index(name = "idx_watch_video_user", columnList = "video_id, user_id"),
                @Index(name = "idx_watch_created_at", columnList = "created_at")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoWatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "last_watched_position", nullable = false)
    private Integer lastWatchedPosition;  // 마지막 시청 위치 (초)

    @Column(name = "total_watched_seconds", nullable = false)
    private Integer totalWatchedSeconds;  // 총 시청 시간 (초)

    @Column(name = "view_counted", nullable = false)
    private Boolean viewCounted;  // 조회수 카운트 여부 (어뷰징이면 false)

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    private VideoWatchHistory(Video video, User user, Integer lastWatchedPosition,
                              Integer totalWatchedSeconds, Boolean viewCounted, String ipAddress) {
        this.video = video;
        this.user = user;
        this.lastWatchedPosition = lastWatchedPosition;
        this.totalWatchedSeconds = totalWatchedSeconds;
        this.viewCounted = viewCounted;
        this.ipAddress = ipAddress;
    }

    /**
     * 시청 기록 생성
     */
    public static VideoWatchHistory create(Video video, User user, String ipAddress, boolean viewCounted) {
        return VideoWatchHistory.builder()
                .video(video)
                .user(user)
                .lastWatchedPosition(0)
                .totalWatchedSeconds(0)
                .viewCounted(viewCounted)
                .ipAddress(ipAddress)
                .build();
    }

    /**
     * 시청 위치 업데이트 (재생 중단 시)
     */
    public void updateWatchPosition(Integer currentPosition, Integer previousPosition) {
        this.lastWatchedPosition = currentPosition;

        // 이번에 본 시간 계산
        int watchedThisTime = currentPosition - previousPosition;

        // 앞으로 간 경우만 누적(되감기는 누적 안함)
        if(watchedThisTime > 0){
            this.totalWatchedSeconds += watchedThisTime;
        }

        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 시청 기록 생성 (시작 위치 지정 - 이어보기용)
     */
    public static VideoWatchHistory createWithStartPosition(
            Video video, User user, String ipAddress, boolean viewCounted, int startPosition) {
        return VideoWatchHistory.builder()
                .video(video)
                .user(user)
                .lastWatchedPosition(startPosition)  // 시작 위치 설정
                .totalWatchedSeconds(0)              // 🔥 아직 0초 시청!
                .viewCounted(viewCounted)
                .ipAddress(ipAddress)
                .build();
    }

    /**
     * 시청 위치만 업데이트 (시청 시간 계산 안함)
     * 건너뛰기, 되감기, 주기적 저장 시 사용
     */
    public void updatePositionOnly(Integer currentPosition) {
        this.lastWatchedPosition = currentPosition;
        this.updatedAt = LocalDateTime.now();
    }
}