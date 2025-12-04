// ========================================
// BatchConfig.java (배치 공통 설정)
// ========================================
package org.sparta.streaming.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    // Spring Batch 기본 설정
    // JobRepository, JobLauncher 등이 자동으로 빈 등록됨
}