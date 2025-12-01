package org.sparta.streaming.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 동영상 목록 응답
@Getter
@AllArgsConstructor
public class VideoListResponse {
    private Integer videoId;
    private String title;
    private String username;
    private Integer videoLengthSeconds;
    private String uploadDate;

    public static VideoListResponse from(org.sparta.streaming.domain.video.entity.Video video) {
        return new VideoListResponse(
                video.getVideoId(),
                video.getTitle(),
                video.getUser().getUsername(),
                video.getVideoLengthSeconds(),
                video.getUploadDate().toString()
        );
    }
}