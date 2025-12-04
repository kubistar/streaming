// ========================================
// SettlementResponse.java (DTO)
// ========================================
package org.sparta.streaming.domain.settlement.dto;

import java.math.BigDecimal;
import java.util.List;

public record SettlementResponse(
        BigDecimal totalVideoAmount,
        BigDecimal totalAdAmount,
        BigDecimal totalAmount,
        List<VideoSettlementResponse> videos
) {}
