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

@Component
// Spring의 Component 어노테이션을 사용하여 이 클래스가 컴포넌트 스캔에 의해 자동으로 감지되고 빈으로 등록되도록 합니다.
public class DataInitializer implements CommandLineRunner {
    // CommandLineRunner 인터페이스를 구현하여, 애플리케이션이 시작될 때 run 메소드가 실행되도록 합니다.

    @Autowired
    private VideoRepository videoRepository;
    // VideoRepository를 자동 주입하여 데이터베이스에 비디오 관련 작업을 할 수 있도록 합니다.

    @Autowired
    private AdRepository adRepository;
    // AdRepository를 자동 주입하여 데이터베이스에 광고 관련 작업을 할 수 있도록 합니다.

    @Autowired
    private AdService adService;
    // 광고 할당 로직을 포함하고 있는 AdService를 자동 주입합니다.

    @Override
    public void run(String... args) throws Exception {
        // CommandLineRunner의 run 메소드 오버라이드. 애플리케이션 시작 시 자동으로 실행됩니다.

        // Create sample videos
        List<Video> videos = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Video video = new Video();
            video.setTitle("Sample Video " + i);  // 비디오 제목 설정
            video.setVideoLength(600 + i * 120);  // 비디오 길이 설정. 모두 10분 이상
            video.setUserId(1L);
            videos.add(video);  // 비디오 목록에 추가
        }
        videoRepository.saveAll(videos);  // 비디오 목록을 데이터베이스에 저장

        // Create sample ads
        for (int i = 1; i <= 5; i++) {
            Ad ad = new Ad();
            ad.setAdType("Type " + i);  // 광고 타입 설정
            ad.setAdTitle("Ad Title " + i);  // 광고 제목 설정
            ad.setAdContent("Ad Content " + i);  // 광고 내용 설정
            ad.setAdUploadDate(LocalDateTime.now());  // 광고 업로드 날짜 설정
            adRepository.save(ad);  // 광고를 데이터베이스에 저장
        }

        // Assign ads to videos
        List<Video> storedVideos = videoRepository.findAll();  // 데이터베이스에서 모든 비디오 조회
        for (Video video : storedVideos) {
            if (video.getVideoLength() >= 300) {  // 비디오 길이가 5분 이상인 경우
                adService.assignRandomAdsToVideo(video.getVideoId(), video.getVideoLength());  // 해당 비디오에 광고 할당
            }
        }
    }
}
