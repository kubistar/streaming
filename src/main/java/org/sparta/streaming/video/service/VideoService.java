package org.sparta.streaming.video.service;

import jakarta.persistence.EntityNotFoundException;
import org.sparta.streaming.video.dto.VideoDto;
import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    public Video uploadVideo(VideoDto videoDto) {
        Video video = new Video();
        video.setUserId(videoDto.getUserId());
        video.setTitle(videoDto.getTitle());
        video.setVideoLength(videoDto.getVideoLength());
        video.setDescription(videoDto.getDescription());
        video.setUploadDate(LocalDateTime.now());
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
}