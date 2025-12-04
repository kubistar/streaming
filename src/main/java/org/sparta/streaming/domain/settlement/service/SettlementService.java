// ========================================
// SettlementService.java
// ========================================
package org.sparta.streaming.domain.settlement.service;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.settlement.dto.SettlementResponse;
import org.sparta.streaming.domain.settlement.dto.VideoSettlementResponse;
import org.sparta.streaming.domain.settlement.entity.DailyVideoSettlement;
import org.sparta.streaming.domain.settlement.repository.DailyVideoSettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final DailyVideoSettlementRepository settlementRepository;

    /**
     * 일간 정산 조회
     */
    public SettlementResponse getDailySettlement(LocalDate date) {
        List<DailyVideoSettlement> settlements = settlementRepository.findBySettlementDate(date);

        return createSettlementResponse(settlements);
    }

    /**
     * 주간 정산 조회
     */
    public SettlementResponse getWeeklySettlement(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Object[]> results = settlementRepository.findSettlementByPeriod(monday, sunday);

        return createSettlementResponseFromAggregate(results);
    }

    /**
     * 월간 정산 조회
     */
    public SettlementResponse getMonthlySettlement(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());

        List<Object[]> results = settlementRepository.findSettlementByPeriod(firstDay, lastDay);

        return createSettlementResponseFromAggregate(results);
    }

    /**
     * 특정 영상의 기간별 정산 조회
     */
    public VideoSettlementResponse getVideoSettlement(Integer videoId, LocalDate startDate, LocalDate endDate) {
        Object[] result = settlementRepository.findTotalSettlementByVideoAndPeriod(videoId, startDate, endDate);

        if (result == null || result[0] == null) {
            return new VideoSettlementResponse(videoId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal videoAmount = (BigDecimal) result[0];
        BigDecimal adAmount = (BigDecimal) result[1];
        BigDecimal totalAmount = (BigDecimal) result[2];

        return new VideoSettlementResponse(videoId, videoAmount, adAmount, totalAmount);
    }

    /**
     * 정산 응답 생성 (일간 데이터)
     */
    private SettlementResponse createSettlementResponse(List<DailyVideoSettlement> settlements) {
        BigDecimal totalVideoAmount = BigDecimal.ZERO;
        BigDecimal totalAdAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<VideoSettlementResponse> videoSettlements = settlements.stream()
                .map(s -> {
                    return new VideoSettlementResponse(
                            s.getVideo().getVideoId(),
                            s.getVideoAmount(),
                            s.getAdAmount(),
                            s.getTotalAmount()
                    );
                })
                .collect(Collectors.toList());

        for (DailyVideoSettlement s : settlements) {
            totalVideoAmount = totalVideoAmount.add(s.getVideoAmount());
            totalAdAmount = totalAdAmount.add(s.getAdAmount());
            totalAmount = totalAmount.add(s.getTotalAmount());
        }

        return new SettlementResponse(
                totalVideoAmount,
                totalAdAmount,
                totalAmount,
                videoSettlements
        );
    }

    /**
     * 정산 응답 생성 (집계 데이터)
     */
    private SettlementResponse createSettlementResponseFromAggregate(List<Object[]> results) {
        BigDecimal totalVideoAmount = BigDecimal.ZERO;
        BigDecimal totalAdAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<VideoSettlementResponse> videoSettlements = results.stream()
                .map(r -> {
                    Integer videoId = (Integer) r[0];
                    BigDecimal videoAmount = (BigDecimal) r[2];
                    BigDecimal adAmount = (BigDecimal) r[3];
                    BigDecimal total = (BigDecimal) r[4];

                    return new VideoSettlementResponse(videoId, videoAmount, adAmount, total);
                })
                .collect(Collectors.toList());

        for (Object[] r : results) {
            totalVideoAmount = totalVideoAmount.add((BigDecimal) r[2]);
            totalAdAmount = totalAdAmount.add((BigDecimal) r[3]);
            totalAmount = totalAmount.add((BigDecimal) r[4]);
        }

        return new SettlementResponse(
                totalVideoAmount,
                totalAdAmount,
                totalAmount,
                videoSettlements
        );
    }
}