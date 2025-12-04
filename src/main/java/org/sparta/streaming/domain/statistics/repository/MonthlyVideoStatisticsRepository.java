// ========================================
// MonthlyVideoStatisticsRepository.java
// ========================================
package org.sparta.streaming.domain.statistics.repository;

import org.sparta.streaming.domain.statistics.entity.MonthlyVideoStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyVideoStatisticsRepository extends JpaRepository<MonthlyVideoStatistics, Long> {

    /**
     * 특정 월의 조회수 TOP5
     */
    List<MonthlyVideoStatistics> findTop5ByYearAndMonthOrderByViewCountDesc(Integer year, Integer month);

    /**
     * 특정 월의 재생시간 TOP5
     */
    List<MonthlyVideoStatistics> findTop5ByYearAndMonthOrderByTotalWatchSecondsDesc(Integer year, Integer month);
}