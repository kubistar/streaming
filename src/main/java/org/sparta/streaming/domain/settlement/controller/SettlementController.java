// ========================================
// SettlementController.java
// ========================================
package org.sparta.streaming.domain.settlement.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.settlement.dto.SettlementResponse;
import org.sparta.streaming.domain.settlement.dto.VideoSettlementResponse;
import org.sparta.streaming.domain.settlement.service.SettlementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * 일간 정산 조회
     * GET /api/settlement/daily?date=2024-12-01
     */
    @GetMapping("/daily")
    public ResponseEntity<SettlementResponse> getDailySettlement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now().minusDays(1);
        SettlementResponse result = settlementService.getDailySettlement(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 주간 정산 조회
     * GET /api/settlement/weekly?date=2024-12-01
     */
    @GetMapping("/weekly")
    public ResponseEntity<SettlementResponse> getWeeklySettlement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        SettlementResponse result = settlementService.getWeeklySettlement(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 월간 정산 조회
     * GET /api/settlement/monthly?date=2024-12-01
     */
    @GetMapping("/monthly")
    public ResponseEntity<SettlementResponse> getMonthlySettlement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        SettlementResponse result = settlementService.getMonthlySettlement(targetDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 영상의 정산 조회
     * GET /api/settlement/video/1?startDate=2024-12-01&endDate=2024-12-31
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity<VideoSettlementResponse> getVideoSettlement(
            @PathVariable Integer videoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        VideoSettlementResponse result = settlementService.getVideoSettlement(videoId, startDate, endDate);
        return ResponseEntity.ok(result);
    }
}