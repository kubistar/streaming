package org.sparta.streaming.video.service;

import org.sparta.streaming.video.entity.Ad;
import org.sparta.streaming.video.entity.VideoAds;
import org.sparta.streaming.video.repository.AdRepository;
import org.sparta.streaming.video.repository.VideoAdsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class AdService {
    @Autowired
    private AdRepository adRepository;

    @Autowired
    private VideoAdsRepository videoAdsRepository;

    /**
     * 비디오의 길이에 따라 랜덤 광고를 할당합니다.
     * @param videoId 할당할 비디오의 ID
     * @param videoLengthSeconds 비디오의 길이 (초 단위)
     */
    public void assignRandomAdsToVideo(Long videoId, int videoLengthSeconds) {
        int adsCount = videoLengthSeconds / 300;  // 5분마다 광고 하나
        List<Ad> ads = adRepository.findAll();  // 데이터베이스에서 모든 광고를 가져옵니다.

        Random random = new Random();
        for (int i = 0; i < adsCount; i++) {
            Ad ad = ads.get(random.nextInt(ads.size()));  // 랜덤 광고 선택
            VideoAds videoAd = new VideoAds();
            videoAd.setVideoId(videoId);
            videoAd.setAdId(ad.getAdId());
            videoAdsRepository.save(videoAd);  // 비디오에 광고 매핑 정보를 저장
        }
    }
}
