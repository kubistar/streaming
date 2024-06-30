package org.sparta.streaming.video.repository;

import org.sparta.streaming.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}