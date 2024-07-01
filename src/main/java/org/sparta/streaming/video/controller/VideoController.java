package org.sparta.streaming.video.controller;


import org.sparta.streaming.video.dto.VideoDto;
import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;


    //  등록
    @PostMapping("/upload")
    public ResponseEntity<VideoDto> uploadVideo(@RequestBody VideoDto videoDto) {
        Video video = videoService.uploadVideo(videoDto);
        return new ResponseEntity<>(convertToDto(video), HttpStatus.CREATED);
    }


    // 수정
    @PutMapping("/{videoId}")
    public ResponseEntity<VideoDto> updateVideo(@PathVariable Long videoId, @RequestBody VideoDto videoDto) {
        Video updatedVideo = videoService.updateVideo(videoId, videoDto);
        return ResponseEntity.ok(convertToDto(updatedVideo));
    }


    // 삭제
    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return ResponseEntity.ok().build();
    }


    //리스트
    @GetMapping("/list")
    public ResponseEntity<List<VideoDto>> getVisibleVideos() {
        List<Video> videos = videoService.findVisibleVideos();
        List<VideoDto> videoDtos = videos.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(videoDtos);
    }



    // 사용자의 동영상 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VideoDto>> getUserVideos(@PathVariable Long userId) {
        List<VideoDto> videos = videoService.getUserVideos(userId);
        return ResponseEntity.ok(videos);
    }


    // 비디오 상세 정보 조회
    @GetMapping("/{videoId}")
    public ResponseEntity<VideoDto> getVideoDetails(@PathVariable Long videoId) {
        return videoService.getVideoDetails(videoId)
                .map(ResponseEntity::ok) // 비디오 정보가 존재하는 경우
                .orElseGet(() -> ResponseEntity.notFound().build()); // 비디오 정보가 없는 경우
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
        dto.setVisible(video.getIsVisible());
        return dto;
    }
}