// ========================================
// StreamingApplication.java (메인 애플리케이션)
// ========================================
package org.sparta.streaming;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableBatchProcessing  // 배치 활성화
@EnableScheduling       // 스케줄링 활성화
public class StreamingApplication {
    public static void main(String[] args) {
        SpringApplication.run(StreamingApplication.class, args);
    }
}