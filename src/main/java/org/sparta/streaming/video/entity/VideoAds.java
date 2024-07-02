package org.sparta.streaming.video.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class VideoAds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer videoAdsId;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Integer adId;

}