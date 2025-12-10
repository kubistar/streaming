// ========================================
// DummyDataController.java (API로 실행)
// ========================================
package org.sparta.streaming.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.streaming.domain.user.entity.User;
import org.sparta.streaming.domain.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dummy")
@RequiredArgsConstructor
@Slf4j
public class DummyDataController {

    private final DummyDataGenerator generator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // 🔥 추가

    /**
     * 더미 데이터 생성 API
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateDummyData() {
        try {
            generator.generateAllDummyData();

            // 🔥 테스트용 판매자 계정 자동 생성
            createTestSellerAccount();

            return ResponseEntity.ok("더미 데이터 생성 완료! 100만 건 이상 생성되었습니다.\n" +
                    "테스트 계정:\n" +
                    "Email: seller@test.com\n" +
                    "Password: password123");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("더미 데이터 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 테스트용 판매자 계정 생성
     */
    private void createTestSellerAccount() {
        // 이미 있으면 스킵
        if (userRepository.findByEmail("seller@test.com").isPresent()) {
            return;
        }

        String encodedPassword = passwordEncoder.encode("password123");
        User seller = User.createSeller(
                "seller@test.com",
                encodedPassword,
                "TestSeller"
        );
        userRepository.save(seller);

        log.info("✅ 테스트 판매자 계정 생성: seller@test.com / password123");
    }
}