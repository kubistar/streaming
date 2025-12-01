// ========================================
// Ad.java (광고 Entity) - API 없음, DB만
// ========================================
package org.sparta.streaming.domain.ad.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ads")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id")
    private Integer adId;

    @Column(name = "ad_title", nullable = false, length = 255)
    private String adTitle;

    @Column(name = "ad_content", columnDefinition = "TEXT")
    private String adContent;

    @Column(name = "ad_url", nullable = false, length = 500)
    private String adUrl;

    @Column(name = "ad_duration_seconds", nullable = false)
    private Integer adDurationSeconds;

    @CreationTimestamp
    @Column(name = "upload_date", nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    // DB에서 직접 INSERT용 생성자
    public Ad(String adTitle, String adContent, String adUrl, Integer adDurationSeconds) {
        this.adTitle = adTitle;
        this.adContent = adContent;
        this.adUrl = adUrl;
        this.adDurationSeconds = adDurationSeconds;
    }
}
