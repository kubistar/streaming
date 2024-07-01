package org.sparta.streaming.video.repository;

import org.sparta.streaming.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByIsVisibleTrue();

    List<Video> findByUserIdAndIsVisibleTrue(Long userId);
}