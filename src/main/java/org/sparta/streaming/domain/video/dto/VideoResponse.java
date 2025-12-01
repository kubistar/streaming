package org.sparta.streaming.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sparta.streaming.domain.video.entity.Video; // import 확인 필요

@Getter
@AllArgsConstructor
public class VideoResponse {
    private Integer videoId;
    private Integer userId;
    private String username;
    private String title;
    private String videoDescription;
    private Integer videoLengthSeconds;
    private String videoUrl;
    private String uploadDate;

    public static VideoResponse from(Video video) {
        return new VideoResponse(
                video.getVideoId(),
                video.getUser().getUserId(),
                video.getUser().getUsername(),
                video.getTitle(),
                video.getVideoDescription(),
                video.getVideoLengthSeconds(),
                video.getVideoUrl(),
                video.getUploadDate().toString()
        );
    }
}