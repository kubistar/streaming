package org.sparta.streaming.domain.ad.repository;

import org.sparta.streaming.domain.ad.entity.VideoAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoAdsRepository extends JpaRepository<VideoAds, Integer> {

    // 특정 동영상의 광고 목록 (위치순)
    List<VideoAds> findByVideoVideoIdOrderByPositionSecondsAsc(Integer videoId);

    // 특정 동영상의 광고 존재 여부
    boolean existsByVideoVideoId(Integer videoId);

    // 특정 위치 이하의 광고들 조회
    List<VideoAds> findByVideoVideoIdAndPositionSecondsLessThanEqual(
            Integer videoId, Integer position
    );
}