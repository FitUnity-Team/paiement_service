package com.fitunity.paiement_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponseDTO {

    private String refundId;
    private String paiementId;
    private BigDecimal amount;
    private String reason;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
}