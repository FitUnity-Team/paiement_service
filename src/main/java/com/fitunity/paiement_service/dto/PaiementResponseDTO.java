package com.fitunity.paiement_service.dto;

import com.fitunity.paiement_service.enums.PaiementMethod;
import com.fitunity.paiement_service.enums.PaiementStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementResponseDTO {

    private String paiementId;
    private String userId;
    private String referenceId;
    private String referenceType;
    private BigDecimal amount;
    private PaiementStatus status;
    private LocalDateTime paiementDate;
    private PaiementMethod paiementMethod;
    private String currency;
    private String stripePaymentIntentId;
    private String clientSecret;
    private String paypalPaymentId;
    private String paypalApprovalUrl;
}

