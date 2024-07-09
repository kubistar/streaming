package org.sparta.streaming.video.repository;

import org.sparta.streaming.video.entity.AdWatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdWatchHistoryRepository extends JpaRepository<AdWatchHistory, Long> {
    // 기본적인 CRUD 메소드 제공
}