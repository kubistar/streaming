package org.sparta.streaming.video.service;

import org.sparta.streaming.video.entity.AdWatchHistory;
import org.sparta.streaming.video.entity.VideoAds;
import org.sparta.streaming.video.repository.AdWatchHistoryRepository;
import org.sparta.streaming.video.repository.VideoAdsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdWatchHistoryService {

    @Autowired
    private AdWatchHistoryRepository adWatchHistoryRepository;

    @Autowired
    private VideoAdsRepository videoAdsRepository;

    /**
     * 광고 시청 기록을 저장합니다.
     * @param videoId 비디오 ID
     * @param watchedSeconds 시청한 시간(초)
     */
    public void saveAdWatchHistory(Long videoId, long watchedSeconds) {
        // 비디오에 삽입된 광고를 조회합니다.
        List<VideoAds> videoAdsList = videoAdsRepository.findByVideoId(videoId);
        for (VideoAds videoAds : videoAdsList) {
            // 광고가 삽입된 위치까지 시청한 경우 광고 시청 기록을 저장합니다.
            if (watchedSeconds >= videoAds.getAdPosition()) {
                AdWatchHistory adWatchHistory = new AdWatchHistory();
                adWatchHistory.setVideoAdsId(videoAds.getVideoAdsId());
                adWatchHistory.setWatchedAt(LocalDateTime.now().minusSeconds(watchedSeconds - videoAds.getAdPosition()));
                adWatchHistoryRepository.save(adWatchHistory);
            }
        }
    }
}