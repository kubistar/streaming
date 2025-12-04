// ========================================
// StatisticsController.java (통계 서비스 전용)
// ========================================
package org.sparta.streaming.domain.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.statistics.dto.VideoStatisticsResponse;
import org.sparta.streaming.domain.statistics.service.StatisticsService;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Profile("statistics")  // statistics 프로필에서만 활성화
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/daily/views")
    public ResponseEntity<List<VideoStatisticsResponse>> getDailyTopByViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getDailyTopByViews(targetDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/daily/watch-time")
    public ResponseEntity<List<VideoStatisticsResponse>> getDailyTopByWatchTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getDailyTopByWatchTime(targetDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/weekly/views")
    public ResponseEntity<List<VideoStatisticsResponse>> getWeeklyTopByViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getWeeklyTopByViews(targetDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/weekly/watch-time")
    public ResponseEntity<List<VideoStatisticsResponse>> getWeeklyTopByWatchTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getWeeklyTopByWatchTime(targetDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly/views")
    public ResponseEntity<List<VideoStatisticsResponse>> getMonthlyTopByViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getMonthlyTopByViews(targetDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly/watch-time")
    public ResponseEntity<List<VideoStatisticsResponse>> getMonthlyTopByWatchTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getMonthlyTopByWatchTime(targetDate);
        return ResponseEntity.ok(result);
    }
}
