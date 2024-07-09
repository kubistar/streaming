package org.sparta.streaming.video.repository;


import org.sparta.streaming.video.entity.VideoAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoAdsRepository extends JpaRepository<VideoAds, Integer> {
    // 추가 메서드 정의 가능

    List<VideoAds> findByVideoId(Long videoId);
    boolean existsByVideoIdAndAdIdAndAdPosition(Long videoId, Integer adId, int adPosition);
}
