package org.sparta.streaming.video.repository;

import org.sparta.streaming.video.entity.VideoWatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoWatchHistoryRepository extends JpaRepository<VideoWatchHistory, Long> {
    Optional<VideoWatchHistory> findTopByUserIdOrderByStartTimeDesc(Long userId);

    Optional<VideoWatchHistory> findLastUnfinishedByUserIdAndVideoId(Long userId, Long videoId);
}