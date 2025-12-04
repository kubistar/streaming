// ========================================
// VideoStatisticsResponse.java (DTO)
// ========================================
package org.sparta.streaming.domain.statistics.dto;

public record VideoStatisticsResponse(
        Integer videoId,
        String title,
        Integer viewCount,
        Long totalWatchSeconds
) {}