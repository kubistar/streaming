package org.sparta.streaming.video.service;

import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.entity.VideoWatchHistory;
import org.sparta.streaming.video.repository.VideoRepository;
import org.sparta.streaming.video.repository.VideoWatchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        List<VideoWatchHistory> histories = watchHistoryRepository.findLastUnfinishedByUserIdAndVideoId(userId, videoId);
        if (!histories.isEmpty() && !isVideoCompleted(histories.get(0))) {
            VideoWatchHistory watchHistory = histories.get(0);
            // 이전 시청 기록에서 계속 재생
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

            // 비디오 정보 가져오기
            Video video = videoRepository.findById(watchHistory.getVideoId())
                    .orElseThrow(() -> new RuntimeException("Video not found for ID: " + watchHistory.getVideoId()));

            // 시청 종료 시간 설정
            LocalDateTime now = LocalDateTime.now();
            watchHistory.setEndTime(now);

            // 이 세션에서의 시청 시간 계산
            long sessionWatchedSeconds = ChronoUnit.SECONDS.between(watchHistory.getStartTime(), now);

            // 총 시청 시간 계산
            long totalWatchedSeconds = (watchHistory.getWatchedTimeSeconds() != null ? watchHistory.getWatchedTimeSeconds() : 0) + sessionWatchedSeconds;

            // 비디오 길이를 초과하지 않도록 시청 시간 제한
            totalWatchedSeconds = Math.min(totalWatchedSeconds, video.getVideoLength());

            // 시청 기록 저장
            watchHistory.setWatchedTimeSeconds(totalWatchedSeconds);
            repository.save(watchHistory);

            // 비디오를 완전히 시청했는지 확인 후 리셋
            if (totalWatchedSeconds >= video.getVideoLength()) {
                // 새로운 시청 기록을 시작할 때 기존 시청 기록을 리셋
                VideoWatchHistory newWatchHistory = new VideoWatchHistory();
                newWatchHistory.setUserId(userId);
                newWatchHistory.setVideoId(video.getVideoId());
                newWatchHistory.setStartTime(LocalDateTime.now());
                newWatchHistory.setWatchedTimeSeconds(0L);
//                repository.save(newWatchHistory);
                return newWatchHistory;
            }

            return watchHistory;
        } else {
            throw new RuntimeException("No active watch history found for user ID: " + userId);
        }
    }





}
