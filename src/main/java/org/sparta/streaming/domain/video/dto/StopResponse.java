package org.sparta.streaming.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StopResponse {
    private Integer videoId;
    private Integer stoppedAt;  // 멈춘 위치 (초)
    private Integer adsWatched;  // 시청한 광고 수
    private String message;
}