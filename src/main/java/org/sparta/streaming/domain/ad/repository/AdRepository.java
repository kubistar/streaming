// ========================================
// Repository들
// ========================================
package org.sparta.streaming.domain.ad.repository;

import org.sparta.streaming.domain.ad.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {
    // 랜덤 광고 하나 가져오기 (실제로는 여러 광고 중 선택 로직 필요)
    Optional<Ad> findFirstByOrderByAdIdAsc();
}