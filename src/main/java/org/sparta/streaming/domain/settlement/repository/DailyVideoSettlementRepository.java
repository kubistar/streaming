// ========================================
// DailyVideoSettlementRepository.java
// ========================================
package org.sparta.streaming.domain.settlement.repository;

import org.sparta.streaming.domain.settlement.entity.DailyVideoSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DailyVideoSettlementRepository extends JpaRepository<DailyVideoSettlement, Long> {

    /**
     * 특정 날짜의 영상별 정산 조회
     */
    List<DailyVideoSettlement> findBySettlementDate(LocalDate settlementDate);

    /**
     * 특정 크리에이터의 특정 날짜 정산 조회
     */
    List<DailyVideoSettlement> findByUserUserIdAndSettlementDate(Integer userId, LocalDate settlementDate);

    /**
     * 특정 영상의 특정 날짜 정산 조회
     */
    DailyVideoSettlement findByVideoVideoIdAndSettlementDate(Integer videoId, LocalDate settlementDate);

    /**
     * 기간별 영상별 정산 합계
     */
    @Query("""
        SELECT s.video.videoId, s.video.title, 
               SUM(s.videoAmount) as videoAmount,
               SUM(s.adAmount) as adAmount,
               SUM(s.totalAmount) as totalAmount
        FROM DailyVideoSettlement s
        WHERE s.settlementDate BETWEEN :startDate AND :endDate
        GROUP BY s.video.videoId, s.video.title
        ORDER BY totalAmount DESC
        """)
    List<Object[]> findSettlementByPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 기간별 크리에이터별 정산 합계
     */
    @Query("""
        SELECT s.user.userId, s.user.username,
               SUM(s.videoAmount) as videoAmount,
               SUM(s.adAmount) as adAmount,
               SUM(s.totalAmount) as totalAmount
        FROM DailyVideoSettlement s
        WHERE s.settlementDate BETWEEN :startDate AND :endDate
        GROUP BY s.user.userId, s.user.username
        ORDER BY totalAmount DESC
        """)
    List<Object[]> findSettlementByCreatorAndPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 특정 영상의 기간별 정산 합계
     */
    @Query("""
        SELECT SUM(s.videoAmount), SUM(s.adAmount), SUM(s.totalAmount)
        FROM DailyVideoSettlement s
        WHERE s.video.videoId = :videoId
          AND s.settlementDate BETWEEN :startDate AND :endDate
        """)
    Object[] findTotalSettlementByVideoAndPeriod(
            @Param("videoId") Integer videoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}