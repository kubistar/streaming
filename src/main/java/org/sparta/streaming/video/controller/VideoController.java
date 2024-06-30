package org.sparta.streaming.video.controller;


import org.sparta.streaming.video.dto.VideoDto;
import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<VideoDto> uploadVideo(@RequestBody VideoDto videoDto) {
        Video video = videoService.uploadVideo(videoDto);
        return new ResponseEntity<>(convertToDto(video), HttpStatus.CREATED);
    }

    @PutMapping("/{videoId}")
    public ResponseEntity<VideoDto> updateVideo(@PathVariable Long videoId, @RequestBody VideoDto videoDto) {
        Video updatedVideo = videoService.updateVideo(videoId, videoDto);
        return ResponseEntity.ok(convertToDto(updatedVideo));
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