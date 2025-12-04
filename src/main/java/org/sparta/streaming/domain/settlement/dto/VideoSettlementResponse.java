// ========================================
// VideoSettlementResponse.java (DTO)
// ========================================
package org.sparta.streaming.domain.settlement.dto;

import java.math.BigDecimal;

public record VideoSettlementResponse(
        Integer videoId,
        BigDecimal videoAmount,
        BigDecimal adAmount,
        BigDecimal totalAmount
) {}