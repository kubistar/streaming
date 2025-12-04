// ========================================
// WeeklyVideoStatisticsRepository.java
// ========================================
package org.sparta.streaming.domain.statistics.repository;

import org.sparta.streaming.domain.statistics.entity.WeeklyVideoStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyVideoStatisticsRepository extends JpaRepository<WeeklyVideoStatistics, Long> {

    /**
     * 특정 주의 조회수 TOP5
     */
    List<WeeklyVideoStatistics> findTop5ByWeekStartDateOrderByViewCountDesc(LocalDate weekStartDate);

    /**
     * 특정 주의 재생시간 TOP5
     */
    List<WeeklyVideoStatistics> findTop5ByWeekStartDateOrderByTotalWatchSecondsDesc(LocalDate weekStartDate);
}