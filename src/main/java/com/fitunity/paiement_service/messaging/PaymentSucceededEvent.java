package com.fitunity.paiement_service.messaging;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSucceededEvent {

    private String paiementId;
    private String userId;
    private String referenceId;
    private String referenceType;
    private BigDecimal amount;
    private String currency;
    private String paiementMethod;
    private LocalDateTime paiementDate;
}
