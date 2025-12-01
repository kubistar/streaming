package org.sparta.streaming.domain.video.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StopRequest {
    private Integer currentPosition;  // 현재 재생 위치 (초)
}