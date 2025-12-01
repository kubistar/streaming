// ========================================
// VideoController.java
// ========================================
package org.sparta.streaming.domain.video.controller;

import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.user.security.UserDetailsImpl;
import org.sparta.streaming.domain.video.dto.VideoListResponse;
import org.sparta.streaming.domain.video.dto.VideoResponse;
import org.sparta.streaming.domain.video.dto.VideoUpdateRequest;
import org.sparta.streaming.domain.video.dto.VideoUploadRequest;
import org.sparta.streaming.domain.video.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * 동영상 업로드
     */
    @PostMapping
    public ResponseEntity<VideoResponse> uploadVideo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody VideoUploadRequest request) {

        VideoResponse response = videoService.uploadVideo(
                userDetails.getUser().getUserId(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 동영상 상세 조회
     */
    @GetMapping("/{videoId}")
    public ResponseEntity<VideoResponse> getVideo(@PathVariable Integer videoId) {
        VideoResponse response = videoService.getVideo(videoId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동영상 목록 조회 (전체)
     */
    @GetMapping
    public ResponseEntity<List<VideoListResponse>> getAllVideos() {
        List<VideoListResponse> response = videoService.getAllVideos();
        return ResponseEntity.ok(response);
    }

    /**
     * 내 동영상 목록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<VideoListResponse>> getMyVideos(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<VideoListResponse> response = videoService.getUserVideos(
                userDetails.getUser().getUserId()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자의 동영상 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VideoListResponse>> getUserVideos(@PathVariable Integer userId) {
        List<VideoListResponse> response = videoService.getUserVideos(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동영상 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<VideoListResponse>> searchVideos(@RequestParam String keyword) {
        List<VideoListResponse> response = videoService.searchVideos(keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * 동영상 수정
     */
    @PutMapping("/{videoId}")
    public ResponseEntity<VideoResponse> updateVideo(
            @PathVariable Integer videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody VideoUpdateRequest request) {

        VideoResponse response = videoService.updateVideo(
                videoId,
                userDetails.getUser().getUserId(),
                request
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 동영상 삭제
     */
    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable Integer videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        videoService.deleteVideo(
                videoId,
                userDetails.getUser().getUserId()
        );

        return ResponseEntity.noContent().build();
    }
}