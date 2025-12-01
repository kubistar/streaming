// ========================================
// StreamingService.java
// ========================================
package org.sparta.streaming.domain.video.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.streaming.domain.ad.entity.Ad;
import org.sparta.streaming.domain.ad.entity.AdWatchHistory;
import org.sparta.streaming.domain.ad.entity.VideoAds;
import org.sparta.streaming.domain.ad.repository.AdRepository;
import org.sparta.streaming.domain.ad.repository.AdWatchHistoryRepository;
import org.sparta.streaming.domain.ad.repository.VideoAdsRepository;
import org.sparta.streaming.domain.user.entity.User;
import org.sparta.streaming.domain.video.dto.PlayResponse;
import org.sparta.streaming.domain.video.dto.StopRequest;
import org.sparta.streaming.domain.video.dto.StopResponse;
import org.sparta.streaming.domain.video.entity.Video;
import org.sparta.streaming.domain.video.entity.VideoWatchHistory;
import org.sparta.streaming.domain.video.repository.VideoRepository;
import org.sparta.streaming.domain.video.repository.VideoWatchHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StreamingService {

    private final VideoRepository videoRepository;
    private final VideoWatchHistoryRepository watchHistoryRepository;
    private final VideoAdsRepository videoAdsRepository;
    private final AdRepository adRepository;
    private final AdWatchHistoryRepository adWatchHistoryRepository;



    /**
     * 동영상 재생 시작
     */
    @Transactional
    public PlayResponse playVideo(Integer videoId, User user, HttpServletRequest request) {
        String ipAddress = getClientIp(request);

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동영상입니다."));

        // 1. 광고 자동 등록 (아직 없으면)
        autoInsertAds(video);

        // 2. 어뷰징 체크
        boolean isAbusing = checkAbusing(video, user, ipAddress);

        // 3. 이전 시청 기록 확인
        Optional<VideoWatchHistory> lastHistory = watchHistoryRepository
                .findTopByVideoVideoIdAndUserUserIdOrderByCreatedAtDesc(videoId, user.getUserId());

        int startPosition = 0;

        // 🔥 이전 기록이 있고 끝까지 안 봤으면 이어보기 위치만 가져오기
        if (lastHistory.isPresent() &&
                lastHistory.get().getLastWatchedPosition() < video.getVideoLengthSeconds()) {
            startPosition = lastHistory.get().getLastWatchedPosition();
            log.info("=== 이어보기 위치: {}초 ===", startPosition);
        }

        // 🔥🔥 항상 새로운 시청 기록 생성(시작 위치만 설정)
        VideoWatchHistory newWatchHistory = VideoWatchHistory.createWithStartPosition(
                video, user, ipAddress, !isAbusing, startPosition);
        watchHistoryRepository.save(newWatchHistory);

        System.out.println("=== 새 시청 세션 생성 (ID: " + newWatchHistory.getRecordId() + ") ===");
        System.out.println("시작 위치: " + startPosition + "초");

        return new PlayResponse(
                videoId,
                video.getTitle(),
                video.getVideoLengthSeconds(),
                startPosition,
                isAbusing ? "어뷰징으로 감지되어 조회수가 카운트되지 않습니다." : "재생 시작"
        );
    }

    /**
     * 동영상 재생 중단 (광고 처리)
     */
    @Transactional
    public StopResponse stopVideo(Integer videoId, User user, StopRequest stopRequest, HttpServletRequest request) {
        String ipAddress = getClientIp(request);

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동영상입니다."));

        // 1. 최근 시청 기록 조회
        VideoWatchHistory watchHistory = watchHistoryRepository
                .findTopByVideoVideoIdAndUserUserIdOrderByCreatedAtDesc(videoId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("시청 기록이 없습니다."));

        // 2. 이전 시청 위치
        int previousPosition = watchHistory.getLastWatchedPosition();
        int currentPosition = validatePosition(stopRequest.getCurrentPosition(), video.getVideoLengthSeconds());

        // 3. 시청 위치 업데이트 (이전 위치 전달)
        watchHistory.updateWatchPosition(currentPosition, previousPosition);

        // 4. 어뷰징 여부 확인
        boolean isAbusing = !watchHistory.getViewCounted();

        // 5. 광고 시청 처리 (어뷰징 아니면)
        int adsWatched = 0;
        if (!isAbusing) {
            adsWatched = processAdWatch(video, user, previousPosition, currentPosition, ipAddress);
        }

        return new StopResponse(
                videoId,
                currentPosition,
                adsWatched,
                isAbusing ? "어뷰징으로 감지되어 광고 시청이 카운트되지 않습니다." :
                        adsWatched + "개의 광고 시청이 기록되었습니다."
        );
    }

    /**
     * 현재 위치만 업데이트 (광고 처리 없음)
     * 건너뛰기, 되감기, 주기적 저장 시 사용
     */
    @Transactional
    public void updatePosition(Integer videoId, User user, StopRequest stopRequest, HttpServletRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동영상입니다."));

        // 최근 시청 기록 조회
        VideoWatchHistory watchHistory = watchHistoryRepository
                .findTopByVideoVideoIdAndUserUserIdOrderByCreatedAtDesc(videoId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("시청 기록이 없습니다."));

        // 이전 위치 저장
        int previousPosition = watchHistory.getLastWatchedPosition();

        // 위치 검증 후 업데이트
        int currentPosition = validatePosition(stopRequest.getCurrentPosition(), video.getVideoLengthSeconds());
        watchHistory.updateWatchPosition(currentPosition, previousPosition);
    }

    /**
     * 위치 검증 (동영상 길이 범위 내로 제한)
     */
    private int validatePosition(int position, int videoLength) {
        if (position < 0) {
            return 0;
        }
        if (position > videoLength) {
            return videoLength;
        }
        return position;
    }

    /**
     * 광고 자동 등록 (5분마다)
     */
    private void autoInsertAds(Video video) {
        // 이미 광고가 등록되어 있으면 스킵
        if (videoAdsRepository.existsByVideoVideoId(video.getVideoId())) {
            return;
        }

        // 광고 하나 가져오기 (실제로는 여러 광고 중 랜덤 선택 등)
        Optional<Ad> adOpt = adRepository.findFirstByOrderByAdIdAsc();
        if (adOpt.isEmpty()) {
            return;  // 등록된 광고가 없음
        }
        Ad ad = adOpt.get();

        int videoLength = video.getVideoLengthSeconds();

        // 5분(300초)마다 광고 삽입
        // 5분 초과 → 5분에 1개
        // 10분 초과 → 5분, 10분에 2개
        for (int position = 300; position < videoLength; position += 300) {
            VideoAds videoAds = VideoAds.create(video, ad, position);
            videoAdsRepository.save(videoAds);
        }
    }

    /**
     * 어뷰징 체크
     */
    private boolean checkAbusing(Video video, User user, String ipAddress) {
        // 1. 본인 동영상인 경우
        if (video.isUploadedBy(user.getUserId())) {
            return true;
        }

        // 2. 30초 이내 동일 IP 접속
        LocalDateTime thirtySecondsAgo = LocalDateTime.now().minusSeconds(30);
        if (watchHistoryRepository.existsByVideoVideoIdAndIpAddressAndCreatedAtAfter(
                video.getVideoId(), ipAddress, thirtySecondsAgo)) {
            return true;
        }

        return false;
    }

    /**
     * 광고 시청 처리
     */
    private int processAdWatch(Video video, User user, int previousPosition, int currentPosition, String ipAddress) {
        System.out.println("=== 광고 시청 처리 시작 ===");
        System.out.println("이전 위치: " + previousPosition + "초, 현재 위치: " + currentPosition + "초");

        // 전체 광고 조회
        List<VideoAds> allAds = videoAdsRepository
                .findByVideoVideoIdOrderByPositionSecondsAsc(video.getVideoId());

        System.out.println("동영상의 전체 광고: " + allAds.size() + "개");
        for (VideoAds ad : allAds) {
            System.out.println("  - " + ad.getPositionSeconds() + "초에 광고");
        }

        // 이번 구간에서 통과한 광고들
        List<VideoAds> adsToWatch = allAds.stream()
                .filter(va -> {
                    boolean passed = va.getPositionSeconds() > previousPosition &&
                            va.getPositionSeconds() <= currentPosition;
                    System.out.println("  광고 " + va.getPositionSeconds() + "초: " +
                            previousPosition + " < " + va.getPositionSeconds() + " <= " + currentPosition +
                            " = " + passed);
                    return passed;
                })
                .toList();

        System.out.println("이번에 통과한 광고: " + adsToWatch.size() + "개");

        int count = 0;
        for (VideoAds videoAds : adsToWatch) {
            // 🔥 중복 체크 제거 - 매번 기록!
            // 통계/정산 시 어뷰징 체크하고 집계
            AdWatchHistory adWatch = AdWatchHistory.create(videoAds, user, ipAddress, true);
            adWatchHistoryRepository.save(adWatch);
            count++;
            System.out.println("    → ✅ 광고 시청 기록 저장!");
        }

        System.out.println("총 새로 기록된 광고: " + count + "개");
        System.out.println("===================");
        return count;
    }

    /**
     * 클라이언트 IP 가져오기
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
