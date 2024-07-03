package org.sparta.streaming.video.repository;

import org.sparta.streaming.video.entity.VideoWatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoWatchHistoryRepository extends JpaRepository<VideoWatchHistory, Long> {
    Optional<VideoWatchHistory> findTopByUserIdOrderByStartTimeDesc(Long userId);

    //Optional<VideoWatchHistory> findLastUnfinishedByUserIdAndVideoId(Long userId, Long videoId);

    // JPA Repository
    @Query("SELECT v FROM VideoWatchHistory v WHERE v.userId = :userId AND v.videoId = :videoId AND v.endTime IS NULL ORDER BY v.startTime DESC")
    List<VideoWatchHistory> findLastUnfinishedByUserIdAndVideoId(@Param("userId") Long userId, @Param("videoId") Long videoId);
}