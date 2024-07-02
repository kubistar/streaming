package org.sparta.streaming.video.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparta.streaming.video.dto.ResponseMessage;
import org.sparta.streaming.video.dto.VideoDto;
import org.sparta.streaming.video.entity.Video;
import org.sparta.streaming.video.entity.VideoWatchHistory;
import org.sparta.streaming.video.service.VideoService;
import org.sparta.streaming.video.service.VideoWatchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoWatchHistoryService watchHistoryService;

    private static final Logger logger = LogManager.getLogger(VideoController.class);

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


    // 비디오 재생 시작
    @PostMapping("/start/{videoId}")
    public ResponseEntity<ResponseMessage> startVideo(@PathVariable Long videoId, @RequestHeader("userId") Long userId) {
        // 디버그 로그를 사용하여 시작 중인 비디오와 사용자 ID를 출력
        logger.debug("Starting video for user ID: {}", userId);

        // 비디오 시청 기록을 시작하는 서비스 메서드를 호출하고, 결과로 비디오 시청 기록 객체를 받음.
        VideoWatchHistory watchHistory = watchHistoryService.startWatching(userId, videoId);

        // 성공 메시지와 함께 비디오 시청 기록 객체를 포함하는 ResponseEntity 객체를 생성하여 반환.
        return ResponseEntity.ok(new ResponseMessage("Video started successfully", watchHistory));
    }


    // 비디오 정지
    @PostMapping("/stop")
    public ResponseEntity<ResponseMessage> stopVideo(@RequestHeader("userId") Long userId) {
        try {
            VideoWatchHistory watchHistory = watchHistoryService.stopWatching(userId);
            return ResponseEntity.ok(new ResponseMessage("Video stopped successfully", watchHistory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Error stopping video: " + e.getMessage(), null));
        }
    }
}