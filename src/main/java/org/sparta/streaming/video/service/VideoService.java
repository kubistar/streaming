package org.sparta.streaming.video.service;

import jakarta.persistence.EntityNotFoundException;
import org.sparta.streaming.user.repository.UserRepository;
import org.sparta.streaming.user.service.UserService;
import org.sparta.streaming.video.dto.VideoDto;
import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserService userService;

    public Video uploadVideo(VideoDto videoDto) {
        Video video = new Video();
        video.setUserId(videoDto.getUserId());
        video.setTitle(videoDto.getTitle());
        video.setVideoLength(videoDto.getVideoLength());
        video.setDescription(videoDto.getDescription());
        video.setUploadDate(LocalDateTime.now());

        // 사용자 권한 업데이트
        userService.updateUserToUploader(videoDto.getUserId());
        return videoRepository.save(video);
    }

    public Video updateVideo(Long videoId, VideoDto videoDto) {
        Video existingVideo = videoRepository.findById(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Video not found with id: " + videoId));

        existingVideo.setTitle(videoDto.getTitle());
        existingVideo.setVideoLength(videoDto.getVideoLength());
        existingVideo.setDescription(videoDto.getDescription());
        existingVideo.setLastModifiedDate(LocalDateTime.now());
        return videoRepository.save(existingVideo);
    }

    public Video deleteVideo(Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        video.setIsVisible(false);
        return videoRepository.save(video);
    }


    public List<Video> findVisibleVideos() {
        return videoRepository.findByIsVisibleTrue();
    }


    public List<VideoDto> getUserVideos(Long userId) {
        List<Video> videos = videoRepository.findByUserIdAndIsVisibleTrue(userId);
        return videos.stream().map(this::convertToDto).collect(Collectors.toList());
    }



    public Optional<VideoDto> getVideoDetails(Long videoId) {
        return videoRepository.findById(videoId)
                .map(this::convertToDto); // Optional 비디오 객체를 VideoDto로 변환
    }


    private VideoDto convertToDto(Video video) {
        VideoDto dto = new VideoDto();
        dto.setVideoId(video.getVideoId());
        dto.setUserId(video.getUserId());
        dto.setTitle(video.getTitle());
        dto.setVideoLength(video.getVideoLength());
        dto.setDescription(video.getDescription());
        dto.setUploadDate(video.getUploadDate());
        dto.setLastModifiedDate(video.getLastModifiedDate());
        return dto;
    }
}