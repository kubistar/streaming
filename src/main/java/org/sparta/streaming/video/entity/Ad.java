package org.sparta.streaming.video.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adId;

    private String adType;
    private String adTitle;
    private String adContent;
    private LocalDateTime adUploadDate;
}
