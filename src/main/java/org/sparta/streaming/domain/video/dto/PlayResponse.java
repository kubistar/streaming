// ========================================
// DTO
// ========================================
package org.sparta.streaming.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayResponse {
    private Integer videoId;
    private String title;
    private Integer videoLengthSeconds;
    private Integer startPosition;  // 이어보기 위치
    private String message;
}