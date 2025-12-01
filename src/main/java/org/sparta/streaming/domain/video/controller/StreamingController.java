// ========================================
// StreamingController.java
// ========================================
package org.sparta.streaming.domain.video.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.sparta.streaming.domain.user.security.UserDetailsImpl;
import org.sparta.streaming.domain.video.dto.PlayResponse;
import org.sparta.streaming.domain.video.dto.StopRequest;
import org.sparta.streaming.domain.video.dto.StopResponse;
import org.sparta.streaming.domain.video.service.StreamingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;

    /**
     * 동영상 재생 시작
     */
    @PostMapping("/play/{videoId}")
    public ResponseEntity<PlayResponse> playVideo(
            @PathVariable Integer videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest request) {

        PlayResponse response = streamingService.playVideo(
                videoId,
                userDetails.getUser(),
                request
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 동영상 재생 중단
     */
    @PostMapping("/stop/{videoId}")
    public ResponseEntity<StopResponse> stopVideo(
            @PathVariable Integer videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody StopRequest stopRequest,
            HttpServletRequest request) {

        StopResponse response = streamingService.stopVideo(
                videoId,
                userDetails.getUser(),
                stopRequest,
                request
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 위치만 업데이트
     * 건너뛰기, 되감기
     */
    @PostMapping("/update-positon/{videoId}")
    public ResponseEntity<String> updatePoiton(
            @PathVariable Integer videoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody StopRequest stopRequest,
            HttpServletRequest request){


        streamingService.updatePosition(
                videoId,
                userDetails.getUser(),
                stopRequest,
                request
        );

        return ResponseEntity.ok("위치가 저장되었습니다.");
    }
}