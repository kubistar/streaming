package org.sparta.streaming.domain.ad.repository;

import org.sparta.streaming.domain.ad.entity.AdWatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdWatchHistoryRepository extends JpaRepository<AdWatchHistory, Long> {

    // 특정 사용자가 특정 광고를 이미 봤는지
    boolean existsByVideoAdsVideoAdsIdAndUserUserId(Integer videoAdsId, Integer userId);
}