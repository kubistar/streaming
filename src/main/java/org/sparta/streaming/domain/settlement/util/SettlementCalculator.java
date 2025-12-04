// ========================================
// SettlementCalculator.java (누진세 계산 유틸)
// ========================================
package org.sparta.streaming.domain.settlement.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SettlementCalculator {

    // 영상 정산 구간별 단가
    private static final double VIDEO_RATE_UNDER_100K = 1.0;
    private static final double VIDEO_RATE_100K_TO_500K = 1.1;
    private static final double VIDEO_RATE_500K_TO_1M = 1.3;
    private static final double VIDEO_RATE_OVER_1M = 1.5;

    // 광고 정산 구간별 단가
    private static final double AD_RATE_UNDER_100K = 10.0;
    private static final double AD_RATE_100K_TO_500K = 12.0;
    private static final double AD_RATE_500K_TO_1M = 15.0;
    private static final double AD_RATE_OVER_1M = 20.0;

    /**
     * 영상 정산 금액 계산 (누진세)
     * @param previousTotal 전날까지 누적 조회수
     * @param todayViews 오늘 증가분
     * @return 영상 정산 금액 (1원 단위 이하 절사)
     */
    public BigDecimal calculateVideoAmount(int previousTotal, int todayViews) {
        if (todayViews <= 0) {
            return BigDecimal.ZERO;
        }

        int currentTotal = previousTotal + todayViews;
        long totalAmount = 0;

        // 각 구간별로 계산
        totalAmount += calculateTier(previousTotal, currentTotal, 0, 100_000, VIDEO_RATE_UNDER_100K);
        totalAmount += calculateTier(previousTotal, currentTotal, 100_000, 500_000, VIDEO_RATE_100K_TO_500K);
        totalAmount += calculateTier(previousTotal, currentTotal, 500_000, 1_000_000, VIDEO_RATE_500K_TO_1M);
        totalAmount += calculateTier(previousTotal, currentTotal, 1_000_000, Integer.MAX_VALUE, VIDEO_RATE_OVER_1M);

        // 1원 단위 이하 절사
        return BigDecimal.valueOf(totalAmount).setScale(0, RoundingMode.DOWN);
    }

    /**
     * 광고 정산 금액 계산 (누진세)
     * @param previousTotal 전날까지 누적 광고 조회수
     * @param todayViews 오늘 광고 증가분
     * @return 광고 정산 금액 (1원 단위 이하 절사)
     */
    public BigDecimal calculateAdAmount(int previousTotal, int todayViews) {
        if (todayViews <= 0) {
            return BigDecimal.ZERO;
        }

        int currentTotal = previousTotal + todayViews;
        long totalAmount = 0;

        // 각 구간별로 계산
        totalAmount += calculateTier(previousTotal, currentTotal, 0, 100_000, AD_RATE_UNDER_100K);
        totalAmount += calculateTier(previousTotal, currentTotal, 100_000, 500_000, AD_RATE_100K_TO_500K);
        totalAmount += calculateTier(previousTotal, currentTotal, 500_000, 1_000_000, AD_RATE_500K_TO_1M);
        totalAmount += calculateTier(previousTotal, currentTotal, 1_000_000, Integer.MAX_VALUE, AD_RATE_OVER_1M);

        // 1원 단위 이하 절사
        return BigDecimal.valueOf(totalAmount).setScale(0, RoundingMode.DOWN);
    }

    /**
     * 구간별 금액 계산
     * @param previousTotal 이전 누적
     * @param currentTotal 현재 누적
     * @param tierStart 구간 시작
     * @param tierEnd 구간 끝
     * @param rate 단가
     * @return 해당 구간에서의 정산 금액
     */
    private long calculateTier(int previousTotal, int currentTotal,
                               int tierStart, int tierEnd, double rate) {
        // 이전 누적이 이미 이 구간을 넘었으면 0
        if (previousTotal >= tierEnd) {
            return 0;
        }

        // 현재 누적이 이 구간에 도달하지 않았으면 0
        if (currentTotal <= tierStart) {
            return 0;
        }

        // 실제로 이 구간에서 증가한 양 계산
        int effectiveStart = Math.max(previousTotal, tierStart);
        int effectiveEnd = Math.min(currentTotal, tierEnd);
        int viewsInTier = effectiveEnd - effectiveStart;

        return (long) (viewsInTier * rate);
    }

    /**
     * 예시 1) 조회수 55만의 경우 계산
     * 99,999회 X 1원 + 400,000회 X 1.1원 + 50,001회 X 1.3원 = 605,000원
     */
    public void example1() {
        int previousTotal = 0;
        int todayViews = 550_000;

        BigDecimal videoAmount = calculateVideoAmount(previousTotal, todayViews);
        System.out.println("예시 1 - 조회수 55만: " + videoAmount + "원");
        // 출력: 605000원
    }

    /**
     * 예시 2) 1일차 55만, 2일차 60만 (누적 115만)
     * 1일차: 99,999회 X 1원 + 400,000회 X 1.1원 + 50,001회 X 1.3원
     * 2일차: 449,999회 X 1.3원 + 150,001회 X 1.5원
     */
    public void example2() {
        // 1일차
        int day1Previous = 0;
        int day1Views = 550_000;
        BigDecimal day1Amount = calculateVideoAmount(day1Previous, day1Views);
        System.out.println("1일차 정산: " + day1Amount + "원");

        // 2일차
        int day2Previous = 550_000;
        int day2Views = 600_000;
        BigDecimal day2Amount = calculateVideoAmount(day2Previous, day2Views);
        System.out.println("2일차 정산: " + day2Amount + "원");

        // 총 정산
        BigDecimal total = day1Amount.add(day2Amount);
        System.out.println("총 정산: " + total + "원");
    }
}