// ========================================
// DailyVideoStatisticsRepository.java
// ========================================
package org.sparta.streaming.domain.statistics.repository;

import org.sparta.streaming.domain.statistics.entity.DailyVideoStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyVideoStatisticsRepository extends JpaRepository<DailyVideoStatistics, Long> {

    /**
     * 특정 날짜의 조회수 TOP5
     */
    List<DailyVideoStatistics> findTop5ByStatDateOrderByViewCountDesc(LocalDate statDate);

    /**
     * 특정 날짜의 재생시간 TOP5
     */
    List<DailyVideoStatistics> findTop5ByStatDateOrderByTotalWatchSecondsDesc(LocalDate statDate);

    /**
     * 기간별 조회수 집계 TOP5
     */
    @Query("""
        SELECT ds.video, SUM(ds.viewCount) as totalViews, SUM(ds.totalWatchSeconds) as totalWatchTime
        FROM DailyVideoStatistics ds
        WHERE ds.statDate BETWEEN :startDate AND :endDate
        GROUP BY ds.video
        ORDER BY totalViews DESC
        LIMIT 5
        """)
    List<Object[]> findTop5ByPeriodOrderByViewCount(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 기간별 재생시간 집계 TOP5
     */
    @Query("""
        SELECT ds.video, SUM(ds.viewCount) as totalViews, SUM(ds.totalWatchSeconds) as totalWatchTime
        FROM DailyVideoStatistics ds
        WHERE ds.statDate BETWEEN :startDate AND :endDate
        GROUP BY ds.video
        ORDER BY totalWatchTime DESC
        LIMIT 5
        """)
    List<Object[]> findTop5ByPeriodOrderByWatchTime(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}