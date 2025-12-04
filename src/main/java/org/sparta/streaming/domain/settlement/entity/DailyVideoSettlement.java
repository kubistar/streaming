package org.sparta.streaming.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.sparta.streaming.domain.user.entity.User;
import org.sparta.streaming.domain.video.entity.Video;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_video_settlement",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "settlement_date"}),
        indexes = {
                @Index(name = "idx_settlement_date", columnList = "settlement_date"),
                @Index(name = "idx_user_date", columnList = "user_id, settlement_date"),
                @Index(name = "idx_total_amount", columnList = "total_amount DESC")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyVideoSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "total_views", nullable = false)
    private Integer totalViews = 0;

    @Column(name = "previous_total_views", nullable = false)
    private Integer previousTotalViews = 0;

    @Column(name = "today_views", nullable = false)
    private Integer todayViews = 0;

    @Column(name = "total_ad_views", nullable = false)
    private Integer totalAdViews = 0;

    @Column(name = "previous_total_ad_views", nullable = false)
    private Integer previousTotalAdViews = 0;

    @Column(name = "today_ad_views", nullable = false)
    private Integer todayAdViews = 0;

    @Column(name = "video_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal videoAmount = BigDecimal.ZERO;

    @Column(name = "ad_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal adAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private DailyVideoSettlement(Video video, User user, LocalDate settlementDate,
                                 Integer totalViews, Integer previousTotalViews, Integer todayViews,
                                 Integer totalAdViews, Integer previousTotalAdViews, Integer todayAdViews,
                                 BigDecimal videoAmount, BigDecimal adAmount, BigDecimal totalAmount) {
        this.video = video;
        this.user = user;
        this.settlementDate = settlementDate;
        this.totalViews = totalViews;
        this.previousTotalViews = previousTotalViews;
        this.todayViews = todayViews;
        this.totalAdViews = totalAdViews;
        this.previousTotalAdViews = previousTotalAdViews;
        this.todayAdViews = todayAdViews;
        this.videoAmount = videoAmount;
        this.adAmount = adAmount;
        this.totalAmount = totalAmount;
    }

    public static DailyVideoSettlement create(Video video, User user, LocalDate settlementDate,
                                              Integer totalViews, Integer previousTotalViews, Integer todayViews,
                                              Integer totalAdViews, Integer previousTotalAdViews, Integer todayAdViews,
                                              BigDecimal videoAmount, BigDecimal adAmount) {
        BigDecimal total = videoAmount.add(adAmount);

        return DailyVideoSettlement.builder()
                .video(video)
                .user(user)
                .settlementDate(settlementDate)
                .totalViews(totalViews)
                .previousTotalViews(previousTotalViews)
                .todayViews(todayViews)
                .totalAdViews(totalAdViews)
                .previousTotalAdViews(previousTotalAdViews)
                .todayAdViews(todayAdViews)
                .videoAmount(videoAmount)
                .adAmount(adAmount)
                .totalAmount(total)
                .build();
    }
}