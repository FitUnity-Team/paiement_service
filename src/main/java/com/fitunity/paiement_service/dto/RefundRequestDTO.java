package com.fitunity.paiement_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestDTO {

    @NotBlank(message = "L'ID du paiement est obligatoire")
    private String paiementId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;

    @NotBlank(message = "La raison est obligatoire")
    private String reason;
}
