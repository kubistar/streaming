// ========================================
// StatisticsController.java
// ========================================
package org.sparta.streaming.domain.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.statistics.dto.VideoStatisticsResponse;
import org.sparta.streaming.domain.statistics.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 일간 조회수 TOP5
     * GET /api/statistics/daily/views?date=2024-12-01
     */
    @GetMapping("/daily/views")
    public ResponseEntity<List<VideoStatisticsResponse>> getDailyTopByViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getDailyTopByViews(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 일간 재생시간 TOP5
     * GET /api/statistics/daily/watch-time?date=2024-12-01
     */
    @GetMapping("/daily/watch-time")
    public ResponseEntity<List<VideoStatisticsResponse>> getDailyTopByWatchTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getDailyTopByWatchTime(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 주간 조회수 TOP5
     * GET /api/statistics/weekly/views?date=2024-12-01
     */
    @GetMapping("/weekly/views")
    public ResponseEntity<List<VideoStatisticsResponse>> getWeeklyTopByViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getWeeklyTopByViews(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 주간 재생시간 TOP5
     * GET /api/statistics/weekly/watch-time?date=2024-12-01
     */
    @GetMapping("/weekly/watch-time")
    public ResponseEntity<List<VideoStatisticsResponse>> getWeeklyTopByWatchTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getWeeklyTopByWatchTime(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 월간 조회수 TOP5
     * GET /api/statistics/monthly/views?date=2024-12-01
     */
    @GetMapping("/monthly/views")
    public ResponseEntity<List<VideoStatisticsResponse>> getMonthlyTopByViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getMonthlyTopByViews(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 월간 재생시간 TOP5
     * GET /api/statistics/monthly/watch-time?date=2024-12-01
     */
    @GetMapping("/monthly/watch-time")
    public ResponseEntity<List<VideoStatisticsResponse>> getMonthlyTopByWatchTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VideoStatisticsResponse> result = statisticsService.getMonthlyTopByWatchTime(targetDate);
        return ResponseEntity.ok(result);
    }
}