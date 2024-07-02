package org.sparta.streaming.video.service;

import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.entity.VideoWatchHistory;
import org.sparta.streaming.video.repository.VideoRepository;
import org.sparta.streaming.video.repository.VideoWatchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;


@Service
public class VideoWatchHistoryService {

    @Autowired
    private VideoWatchHistoryRepository repository;

    @Autowired
    private VideoWatchHistoryRepository watchHistoryRepository;

    @Autowired
    private VideoRepository videoRepository;

    /**
     * 이 메서드는 주어진 비디오 시청 기록이 완료되었는지 여부를 판단합니다.
     * 완료 여부는 사용자가 비디오의 총 길이만큼 시청했는지에 대한 시청 시간으로 결정됩니다.
     *
     * @param watchHistory 사용자의 비디오 시청 기록 객체입니다.
     * @return 비디오 시청이 완료되었으면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
     * @throws RuntimeException 비디오가 데이터베이스에서 찾을 수 없을 때 예외를 발생시킵니다.
     */
    private boolean isVideoCompleted(VideoWatchHistory watchHistory) {
        // 비디오 정보를 데이터베이스에서 조회합니다. 해당 비디오 ID로 조회되는 비디오가 없을 경우 예외를 발생시킵니다.
        Video video = videoRepository.findById(watchHistory.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

        // 시청 기록에서 기록된 시청 시간이 비디오의 전체 길이보다 크거나 같은지 확인합니다.
        // 비디오의 전체 길이는 video.getVideoLength() 메소드를 통해 얻습니다.
        return watchHistory.getWatchedTimeSeconds() >= video.getVideoLength();
    }

    public VideoWatchHistory startWatching(Long userId, Long videoId) {
        Optional<VideoWatchHistory> lastWatchHistory = watchHistoryRepository.findLastUnfinishedByUserIdAndVideoId(userId, videoId);

        if (lastWatchHistory.isPresent() && !isVideoCompleted(lastWatchHistory.get())) {
            // 이전 시청 기록에서 계속 재생
            VideoWatchHistory watchHistory = lastWatchHistory.get();
            // 설정된 이전 시청 종료 시간으로부터 재생 시작
            watchHistory.setStartTime(LocalDateTime.now());
            return watchHistory;
        } else {
            // 새로운 시청 기록 생성
            VideoWatchHistory newWatchHistory = new VideoWatchHistory();
            newWatchHistory.setUserId(userId);
            newWatchHistory.setVideoId(videoId);
            newWatchHistory.setStartTime(LocalDateTime.now());
            return watchHistoryRepository.save(newWatchHistory);
        }
    }

    public VideoWatchHistory stopWatching(Long userId) {
        // 사용자의 마지막 활성 시청 기록 찾기
        Optional<VideoWatchHistory> lastWatchHistory = repository.findTopByUserIdOrderByStartTimeDesc(userId);

        if (lastWatchHistory.isPresent()) {
            VideoWatchHistory watchHistory = lastWatchHistory.get();
            watchHistory.setEndTime(LocalDateTime.now());
            long seconds = ChronoUnit.SECONDS.between(watchHistory.getStartTime(), watchHistory.getEndTime());
            watchHistory.setWatchedTimeSeconds(seconds);
            return repository.save(watchHistory);
        } else {
            throw new RuntimeException("No active watch history found for user ID: " + userId);
        }
    }
}
