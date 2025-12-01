// ========================================
// Video.java (Entity)
// ========================================
package org.sparta.streaming.domain.video.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sparta.streaming.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Integer videoId;

    // 연관관계 매핑 (추천)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 업로더

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "video_description", columnDefinition = "TEXT")
    private String videoDescription;

    @Column(name = "video_length_seconds", nullable = false)
    private Integer videoLengthSeconds;

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    @CreationTimestamp
    @Column(name = "upload_date", nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========================================
    // Builder 패턴
    // ========================================
    @Builder
    private Video(User user, String title, String videoDescription,
                  Integer videoLengthSeconds, String videoUrl) {
        this.user = user;
        this.title = title;
        this.videoDescription = videoDescription;
        this.videoLengthSeconds = videoLengthSeconds;
        this.videoUrl = videoUrl;
    }

    // ========================================
    // 정적 팩토리 메서드
    // ========================================

    /**
     * 동영상 생성
     */
    public static Video createVideo(User uploader, String title, String description,
                                    Integer lengthSeconds, String videoUrl) {
        return Video.builder()
                .user(uploader)
                .title(title)
                .videoDescription(description)
                .videoLengthSeconds(lengthSeconds)
                .videoUrl(videoUrl)
                .build();
    }

    // ========================================
    // 비즈니스 로직
    // ========================================

    /**
     * 동영상 정보 수정
     */
    public void updateVideo(String title, String description) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (description != null) {
            this.videoDescription = description;
        }
    }

    /**
     * 동영상 URL 변경
     */
    public void changeVideoUrl(String newVideoUrl) {
        if (newVideoUrl == null || newVideoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("동영상 URL은 비어있을 수 없습니다.");
        }
        this.videoUrl = newVideoUrl;
    }

    /**
     * 업로더 확인
     */
    public boolean isUploadedBy(Integer userId) {
        return this.user.getUserId().equals(userId);
    }
}