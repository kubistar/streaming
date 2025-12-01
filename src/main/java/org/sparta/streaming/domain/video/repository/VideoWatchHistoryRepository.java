package org.sparta.streaming.domain.video.repository;

import org.sparta.streaming.domain.video.entity.VideoWatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VideoWatchHistoryRepository extends JpaRepository<VideoWatchHistory, Long> {

    // 특정 사용자의 특정 동영상 최근 시청 기록
    Optional<VideoWatchHistory> findTopByVideoVideoIdAndUserUserIdOrderByCreatedAtDesc(
            Integer videoId, Integer userId
    );

    // 30초 이내 동일 IP로 시청한 기록 있는지 (어뷰징 체크)
    boolean existsByVideoVideoIdAndIpAddressAndCreatedAtAfter(
            Integer videoId, String ipAddress, LocalDateTime after
    );
}