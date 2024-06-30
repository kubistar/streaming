package org.sparta.streaming.video.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VideoDto {
    private Long videoId;
    private Long userId;
    private String title;
    private Integer videoLength;
    private String description;
    private LocalDateTime uploadDate;
    private LocalDateTime lastModifiedDate;
}