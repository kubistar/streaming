package org.sparta.streaming.video.service;

import org.sparta.streaming.video.entity.Ad;
import org.sparta.streaming.video.entity.VideoAds;
import org.sparta.streaming.video.repository.AdRepository;
import org.sparta.streaming.video.repository.VideoAdsRepository;
import org.sparta.streaming.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class AdService {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private VideoRepository videoRepository;

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
        for (int i = 1; i <= adsCount; i++) {
            Ad ad = ads.get(random.nextInt(ads.size()));  // 랜덤 광고 선택

            // 광고가 이미 할당되지 않은 경우에만 광고를 추가합니다.
            if (!isAdAlreadyAssigned(videoId, ad.getAdId(), i * 300)) {
                VideoAds videoAd = new VideoAds();
                videoAd.setVideoId(videoId);  // 비디오 ID 설정
                videoAd.setAdId(ad.getAdId());  // 광고 ID 설정
                videoAd.setAdPosition(i * 300); // 광고가 삽입될 위치 (초 단위)
                videoAdsRepository.save(videoAd);  // 비디오에 광고 매핑 정보를 저장합니다.
            }
        }
    }

    /**
     * 광고가 이미 할당되었는지 확인하는 메소드입니다.
     * @param videoId 비디오 ID
     * @param adId 광고 ID
     * @param adPosition 광고 위치
     * @return 광고가 이미 할당되었는지 여부
     */
    private boolean isAdAlreadyAssigned(Long videoId, Integer adId, int adPosition) {
        return videoAdsRepository.existsByVideoIdAndAdIdAndAdPosition(videoId, adId, adPosition);
    }
}