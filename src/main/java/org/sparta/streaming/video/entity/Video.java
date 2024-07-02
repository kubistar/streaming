package org.sparta.streaming.video.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

    @Column(nullable = false)
    private Long userId; // FK to User table

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = true)
    private Integer videoLength;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime uploadDate = LocalDateTime.now();;

    @Column(nullable = true)
    private LocalDateTime lastModifiedDate;   //  업데이트한 날짜


    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isVisible = true;
}
