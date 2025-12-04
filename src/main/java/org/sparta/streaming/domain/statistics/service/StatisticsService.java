// ========================================
// StatisticsService.java
// ========================================
package org.sparta.streaming.domain.statistics.service;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.statistics.dto.VideoStatisticsResponse;
import org.sparta.streaming.domain.statistics.entity.DailyVideoStatistics;
import org.sparta.streaming.domain.statistics.repository.DailyVideoStatisticsRepository;
import org.sparta.streaming.domain.video.entity.Video;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final DailyVideoStatisticsRepository statisticsRepository;

    /**
     * 일간 조회수 TOP5
     */
    public List<VideoStatisticsResponse> getDailyTopByViews(LocalDate date) {
        List<DailyVideoStatistics> stats = statisticsRepository
                .findTop5ByStatDateOrderByViewCountDesc(date);

        return stats.stream()
                .map(s -> new VideoStatisticsResponse(
                        s.getVideo().getVideoId(),
                        s.getVideo().getTitle(),
                        s.getViewCount(),
                        s.getTotalWatchSeconds()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 일간 재생시간 TOP5
     */
    public List<VideoStatisticsResponse> getDailyTopByWatchTime(LocalDate date) {
        List<DailyVideoStatistics> stats = statisticsRepository
                .findTop5ByStatDateOrderByTotalWatchSecondsDesc(date);

        return stats.stream()
                .map(s -> new VideoStatisticsResponse(
                        s.getVideo().getVideoId(),
                        s.getVideo().getTitle(),
                        s.getViewCount(),
                        s.getTotalWatchSeconds()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 주간 조회수 TOP5
     */
    public List<VideoStatisticsResponse> getWeeklyTopByViews(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Object[]> results = statisticsRepository
                .findTop5ByPeriodOrderByViewCount(monday, sunday);

        return results.stream()
                .map(r -> new VideoStatisticsResponse(
                        ((Video) r[0]).getVideoId(),
                        ((Video) r[0]).getTitle(),
                        ((Long) r[1]).intValue(),
                        (Long) r[2]
                ))
                .collect(Collectors.toList());
    }

    /**
     * 주간 재생시간 TOP5
     */
    public List<VideoStatisticsResponse> getWeeklyTopByWatchTime(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Object[]> results = statisticsRepository
                .findTop5ByPeriodOrderByWatchTime(monday, sunday);

        return results.stream()
                .map(r -> new VideoStatisticsResponse(
                        ((Video) r[0]).getVideoId(),
                        ((Video) r[0]).getTitle(),
                        ((Long) r[1]).intValue(),
                        (Long) r[2]
                ))
                .collect(Collectors.toList());
    }

    /**
     * 월간 조회수 TOP5
     */
    public List<VideoStatisticsResponse> getMonthlyTopByViews(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());

        List<Object[]> results = statisticsRepository
                .findTop5ByPeriodOrderByViewCount(firstDay, lastDay);

        return results.stream()
                .map(r -> new VideoStatisticsResponse(
                        ((Video) r[0]).getVideoId(),
                        ((Video) r[0]).getTitle(),
                        ((Long) r[1]).intValue(),
                        (Long) r[2]
                ))
                .collect(Collectors.toList());
    }

    /**
     * 월간 재생시간 TOP5
     */
    public List<VideoStatisticsResponse> getMonthlyTopByWatchTime(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());

        List<Object[]> results = statisticsRepository
                .findTop5ByPeriodOrderByWatchTime(firstDay, lastDay);

        return results.stream()
                .map(r -> new VideoStatisticsResponse(
                        ((Video) r[0]).getVideoId(),
                        ((Video) r[0]).getTitle(),
                        ((Long) r[1]).intValue(),
                        (Long) r[2]
                ))
                .collect(Collectors.toList());
    }
}
