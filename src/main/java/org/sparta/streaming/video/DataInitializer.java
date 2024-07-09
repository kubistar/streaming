package org.sparta.streaming.video;

import org.sparta.streaming.video.entity.Ad;
import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.repository.AdRepository;
import org.sparta.streaming.video.repository.VideoRepository;
import org.sparta.streaming.video.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private VideoRepository videoRepository;
//
//    @Autowired
//    private AdRepository adRepository;
//
//    @Autowired
//    private AdService adService;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 비디오 데이터가 비어 있는 경우에만 새 비디오 데이터를 생성합니다.
//        if (videoRepository.count() == 0) {
//            List<Video> videos = new ArrayList<>();
//            for (int i = 1; i <= 10; i++) {
//                Video video = new Video();
//                video.setTitle("Sample Video " + i);  // 비디오 제목 설정
//                video.setVideoLength(600 + i * 120);  // 비디오 길이 설정. 모두 10분 이상
//                video.setUserId(1L);
//                videos.add(video);  // 비디오 목록에 추가
//            }
//            videoRepository.saveAll(videos);  // 비디오 목록을 데이터베이스에 저장
//        }
//
//        // 광고 데이터가 비어 있는 경우에만 새 광고 데이터를 생성합니다.
//        if (adRepository.count() == 0) {
//            for (int i = 1; i <= 5; i++) {
//                Ad ad = new Ad();
//                ad.setAdType("Type " + i);  // 광고 타입 설정
//                ad.setAdTitle("Ad Title " + i);  // 광고 제목 설정
//                ad.setAdContent("Ad Content " + i);  // 광고 내용 설정
//                ad.setAdUploadDate(LocalDateTime.now());  // 광고 업로드 날짜 설정
//                adRepository.save(ad);  // 광고를 데이터베이스에 저장
//            }
//        }
//
//        // 비디오에 이미 광고가 할당되어 있는지 확인하고, 할당되어 있지 않은 경우에만 광고를 할당합니다.
//        List<Video> storedVideos = videoRepository.findAll();
//        for (Video video : storedVideos) {
//            if (video.getVideoLength() >= 300) {  // 비디오 길이가 5분 이상인 경우
//                adService.assignRandomAdsToVideo(video.getVideoId(), video.getVideoLength());  // 해당 비디오에 광고 할당
//            }
//        }
//    }
//}
