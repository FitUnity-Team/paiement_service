package com.fitunity.paiement_service.dto;

import com.fitunity.paiement_service.enums.PaiementMethod;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementRequestDTO {

    @NotBlank(message = "L'ID utilisateur est obligatoire")
    private String userId;

    @NotBlank(message = "L'ID de référence est obligatoire")
    private String referenceId;

    @NotBlank(message = "Le type de référence est obligatoire")
    private String referenceType;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;

    @NotNull(message = "La méthode de paiement est obligatoire")
    private PaiementMethod paiementMethod;

    @NotBlank(message = "La devise est obligatoire")
    @Size(min = 3, max = 3, message = "La devise doit être sur 3 caractères")
    private String currency;
}
