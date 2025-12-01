// ========================================
// AdWatchHistory.java (광고 시청 기록)
// ========================================
package org.sparta.streaming.domain.ad.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.sparta.streaming.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "ad_watch_history",
        indexes = {
                @Index(name = "idx_adwatch_video_ads_user", columnList = "video_ads_id, user_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdWatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_ads_id", nullable = false)
    private VideoAds videoAds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "watched_at", nullable = false)
    private LocalDateTime watchedAt;

    @Column(name = "view_counted", nullable = false)
    private Boolean viewCounted;  // 조회수 카운트 여부 (어뷰징이면 false)

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private AdWatchHistory(VideoAds videoAds, User user, Boolean viewCounted, String ipAddress) {
        this.videoAds = videoAds;
        this.user = user;
        this.watchedAt = LocalDateTime.now();
        this.viewCounted = viewCounted;
        this.ipAddress = ipAddress;
    }

    /**
     * 광고 시청 기록 생성
     */
    public static AdWatchHistory create(VideoAds videoAds, User user, String ipAddress, boolean viewCounted) {
        return AdWatchHistory.builder()
                .videoAds(videoAds)
                .user(user)
                .viewCounted(viewCounted)
                .ipAddress(ipAddress)
                .build();
    }
}