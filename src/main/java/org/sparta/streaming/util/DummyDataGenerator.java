// ========================================
// DummyDataGenerator.java
// ========================================
package org.sparta.streaming.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.streaming.domain.ad.entity.Ad;
import org.sparta.streaming.domain.ad.entity.AdWatchHistory;
import org.sparta.streaming.domain.ad.entity.VideoAds;
import org.sparta.streaming.domain.ad.repository.AdRepository;
import org.sparta.streaming.domain.ad.repository.AdWatchHistoryRepository;
import org.sparta.streaming.domain.ad.repository.VideoAdsRepository;
import org.sparta.streaming.domain.user.entity.User;
import org.sparta.streaming.domain.user.repository.UserRepository;
import org.sparta.streaming.domain.video.entity.Video;
import org.sparta.streaming.domain.video.entity.VideoWatchHistory;
import org.sparta.streaming.domain.video.repository.VideoRepository;
import org.sparta.streaming.domain.video.repository.VideoWatchHistoryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataGenerator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // 🔥 추가!
    private final VideoRepository videoRepository;
    private final VideoWatchHistoryRepository watchHistoryRepository;
    private final AdRepository adRepository;
    private final VideoAdsRepository videoAdsRepository;
    private final AdWatchHistoryRepository adWatchHistoryRepository;

    private final Random random = new Random();

    /**
     * 전체 더미 데이터 생성
     */
    @Transactional
    public void generateAllDummyData() {
        log.info("========================================");
        log.info("더미 데이터 생성 시작!");
        log.info("========================================");

        // 1. 사용자 생성
        List<User> users = generateUsers(10_000);
        log.info("✅ 사용자 10,000명 생성 완료");

        // 2. 동영상 생성
        List<Video> videos = generateVideos(users, 1_000);
        log.info("✅ 동영상 1,000개 생성 완료");

        // 3. 광고 생성
        List<Ad> ads = generateAds(10);
        log.info("✅ 광고 10개 생성 완료");

        // 4. 동영상에 광고 자동 삽입
        generateVideoAds(videos, ads);
        log.info("✅ 동영상-광고 매핑 완료");

        // 5. 시청 기록 생성 (100만 건)
        generateWatchHistory(users, videos, 1_000_000);
        log.info("✅ 시청 기록 1,000,000건 생성 완료");

        // 6. 광고 시청 기록 생성
        generateAdWatchHistory(users, videos, 500_000);
        log.info("✅ 광고 시청 기록 500,000건 생성 완료");

        log.info("========================================");
        log.info("더미 데이터 생성 완료!");
        log.info("========================================");
    }

    /**
     * 사용자 생성 (비밀번호 암호화)
     */
    private List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // 🔥 비밀번호 암호화!
            String encodedPassword = passwordEncoder.encode("password123");

            User user = User.createUser(
                    "user" + i + "@test.com",
                    encodedPassword,  // 🔥 암호화된 비밀번호
                    "User" + i
            );
            users.add(user);

            // 10%는 판매자(크리에이터)로 설정
            if (i % 10 == 0) {
                user.upgradeToSeller();
            }
        }

        return userRepository.saveAll(users);
    }

    /**
     * 동영상 생성
     */
    private List<Video> generateVideos(List<User> users, int count) {
        List<Video> videos = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // 크리에이터 중에서 랜덤 선택
            User creator = users.stream()
                    .filter(User::isSeller)
                    .skip(random.nextInt((int) users.stream().filter(User::isSeller).count()))
                    .findFirst()
                    .orElse(users.get(0));

            // 동영상 길이: 5분 ~ 1시간 랜덤
            int videoLength = 300 + random.nextInt(3300); // 300초(5분) ~ 3600초(1시간)

            Video video = Video.createVideo(
                    creator,
                    "동영상 제목 " + i,
                    "동영상 설명 " + i,
                    videoLength,
                    "https://example.com/video" + i + ".mp4"
            );
            videos.add(video);
        }

        return videoRepository.saveAll(videos);
    }

    /**
     * 광고 생성
     */
    private List<Ad> generateAds(int count) {
        List<Ad> ads = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Ad ad = new Ad(
                    "광고 제목 " + i,
                    "광고 내용 " + i,
                    "https://example.com/ad" + i,
                    15 + (i * 5) // 15초, 20초, 25초 ...
            );
            ads.add(ad);
        }

        return adRepository.saveAll(ads);
    }

    /**
     * 동영상에 광고 자동 삽입 (5분마다)
     */
    private void generateVideoAds(List<Video> videos, List<Ad> ads) {
        List<VideoAds> videoAdsList = new ArrayList<>();

        for (Video video : videos) {
            int videoLength = video.getVideoLengthSeconds();
            Ad randomAd = ads.get(random.nextInt(ads.size()));

            // 5분(300초)마다 광고 삽입
            for (int position = 300; position < videoLength; position += 300) {
                VideoAds videoAds = VideoAds.create(video, randomAd, position);
                videoAdsList.add(videoAds);
            }
        }

        videoAdsRepository.saveAll(videoAdsList);
    }

    /**
     * 시청 기록 생성 (100만 건) - 최근 30일간 분산
     */
    private void generateWatchHistory(List<User> users, List<Video> videos, int totalCount) {
        int batchSize = 1000;
        int batchCount = 0;

        for (int i = 0; i < totalCount; i += batchSize) {
            List<VideoWatchHistory> batch = new ArrayList<>();

            for (int j = 0; j < batchSize && (i + j) < totalCount; j++) {
                User randomUser = users.get(random.nextInt(users.size()));
                Video randomVideo = videos.get(random.nextInt(videos.size()));

                // 최근 30일 중 랜덤 날짜
                LocalDateTime createdAt = LocalDateTime.now()
                        .minusDays(random.nextInt(30))
                        .minusHours(random.nextInt(24))
                        .minusMinutes(random.nextInt(60));

                VideoWatchHistory history = VideoWatchHistory.builder()
                        .video(randomVideo)
                        .user(randomUser)
                        .lastWatchedPosition(random.nextInt(randomVideo.getVideoLengthSeconds()))
                        .totalWatchedSeconds(random.nextInt(randomVideo.getVideoLengthSeconds()))
                        .viewCounted(true)
                        .ipAddress("127.0.0." + random.nextInt(255))
                        .build();

                // createdAt을 수동으로 설정 (리플렉션 사용)
                try {
                    var field = VideoWatchHistory.class.getDeclaredField("createdAt");
                    field.setAccessible(true);
                    field.set(history, createdAt);
                } catch (Exception e) {
                    log.warn("createdAt 설정 실패", e);
                }

                batch.add(history);
            }

            watchHistoryRepository.saveAll(batch);
            batchCount++;

            if (batchCount % 10 == 0) {
                log.info("시청 기록 생성 진행: {}/{}", i + batchSize, totalCount);
            }
        }
    }

    /**
     * 광고 시청 기록 생성 (50만 건)
     */
    private void generateAdWatchHistory(List<User> users, List<Video> videos, int totalCount) {
        List<VideoAds> allVideoAds = videoAdsRepository.findAll();

        if (allVideoAds.isEmpty()) {
            log.warn("광고가 없어서 광고 시청 기록을 생성할 수 없습니다.");
            return;
        }

        int batchSize = 1000;
        int batchCount = 0;

        for (int i = 0; i < totalCount; i += batchSize) {
            List<AdWatchHistory> batch = new ArrayList<>();

            for (int j = 0; j < batchSize && (i + j) < totalCount; j++) {
                User randomUser = users.get(random.nextInt(users.size()));
                VideoAds randomVideoAd = allVideoAds.get(random.nextInt(allVideoAds.size()));

                // 최근 30일 중 랜덤 날짜
                LocalDateTime createdAt = LocalDateTime.now()
                        .minusDays(random.nextInt(30))
                        .minusHours(random.nextInt(24));

                AdWatchHistory adWatch = AdWatchHistory.builder()
                        .videoAds(randomVideoAd)
                        .user(randomUser)
                        .viewCounted(true)
                        .ipAddress("127.0.0." + random.nextInt(255))
                        .build();

                // createdAt 수동 설정
                try {
                    var field = AdWatchHistory.class.getDeclaredField("createdAt");
                    field.setAccessible(true);
                    field.set(adWatch, createdAt);
                } catch (Exception e) {
                    log.warn("createdAt 설정 실패", e);
                }

                batch.add(adWatch);
            }

            adWatchHistoryRepository.saveAll(batch);
            batchCount++;

            if (batchCount % 10 == 0) {
                log.info("광고 시청 기록 생성 진행: {}/{}", i + batchSize, totalCount);
            }
        }
    }
}
