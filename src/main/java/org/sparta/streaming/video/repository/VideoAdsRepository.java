package org.sparta.streaming.video.repository;


import org.sparta.streaming.video.entity.VideoAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoAdsRepository extends JpaRepository<VideoAds, Integer> {
    // 추가 메서드 정의 가능
}
