// ========================================
// VideoAds.java (동영상-광고 매핑) - 자동 생성됨
// ========================================
package org.sparta.streaming.domain.ad.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.sparta.streaming.domain.video.entity.Video;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_ads")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoAds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_ads_id")
    private Integer videoAdsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Column(name = "position_seconds", nullable = false)
    private Integer positionSeconds;  // 광고 삽입 위치 (초)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private VideoAds(Video video, Ad ad, Integer positionSeconds) {
        this.video = video;
        this.ad = ad;
        this.positionSeconds = positionSeconds;
    }

    /**
     * 광고 자동 삽입
     */
    public static VideoAds create(Video video, Ad ad, Integer positionSeconds) {
        return VideoAds.builder()
                .video(video)
                .ad(ad)
                .positionSeconds(positionSeconds)
                .build();
    }
}
