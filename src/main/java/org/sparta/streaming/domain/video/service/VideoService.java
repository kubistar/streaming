// ========================================
// VideoService.java
// ========================================
package org.sparta.streaming.domain.video.service;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.user.entity.User;
import org.sparta.streaming.domain.user.repository.UserRepository;
import org.sparta.streaming.domain.video.dto.VideoListResponse;
import org.sparta.streaming.domain.video.dto.VideoResponse;
import org.sparta.streaming.domain.video.dto.VideoUpdateRequest;
import org.sparta.streaming.domain.video.dto.VideoUploadRequest;
import org.sparta.streaming.domain.video.entity.Video;
import org.sparta.streaming.domain.video.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    /**
     * 동영상 업로드
     */
    @Transactional
    public VideoResponse uploadVideo(Integer userId, VideoUploadRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 동영상 생성
        Video video = Video.createVideo(
                user,
                request.getTitle(),
                request.getVideoDescription(),
                request.getVideoLengthSeconds(),
                request.getVideoUrl()
        );

        // 저장
        Video savedVideo = videoRepository.save(video);

        // 사용자를 판매자로 자동 업그레이드 (첫 업로드 시)
        if (!user.isSeller()) {
            user.upgradeToSeller();
        }

        return VideoResponse.from(savedVideo);
    }

    /**
     * 동영상 상세 조회
     */
    public VideoResponse getVideo(Integer videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동영상입니다."));

        return VideoResponse.from(video);
    }

    /**
     * 동영상 목록 조회 (전체)
     */
    public List<VideoListResponse> getAllVideos() {
        return videoRepository.findAllByOrderByUploadDateDesc()
                .stream()
                .map(VideoListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자의 동영상 목록 조회
     */
    public List<VideoListResponse> getUserVideos(Integer userId) {
        return videoRepository.findByUserUserId(userId)
                .stream()
                .map(VideoListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 동영상 검색
     */
    public List<VideoListResponse> searchVideos(String keyword) {
        return videoRepository.findByTitleContaining(keyword)
                .stream()
                .map(VideoListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 동영상 수정
     */
    @Transactional
    public VideoResponse updateVideo(Integer videoId, Integer userId, VideoUpdateRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동영상입니다."));

        // 권한 확인 (본인만 수정 가능)
        if (!video.isUploadedBy(userId)) {
            throw new IllegalArgumentException("동영상을 수정할 권한이 없습니다.");
        }

        // 수정
        video.updateVideo(request.getTitle(), request.getVideoDescription());

        return VideoResponse.from(video);
    }

    /**
     * 동영상 삭제
     */
    @Transactional
    public void deleteVideo(Integer videoId, Integer userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동영상입니다."));

        // 권한 확인 (본인만 삭제 가능)
        if (!video.isUploadedBy(userId)) {
            throw new IllegalArgumentException("동영상을 삭제할 권한이 없습니다.");
        }

        videoRepository.delete(video);
    }
}
