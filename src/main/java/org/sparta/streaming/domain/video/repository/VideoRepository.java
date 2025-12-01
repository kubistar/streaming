// ========================================
// VideoRepository.java
// ========================================
package org.sparta.streaming.domain.video.repository;

import org.sparta.streaming.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    /**
     * 특정 사용자의 동영상 목록 조회
     */
    List<Video> findByUserUserId(Integer userId);

    /**
     * 제목으로 검색
     */
    List<Video> findByTitleContaining(String keyword);

    /**
     * 최신순 조회
     */
    List<Video> findAllByOrderByUploadDateDesc();
}