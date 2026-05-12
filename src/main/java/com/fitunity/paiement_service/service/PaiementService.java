package com.fitunity.paiement_service.service;

import com.fitunity.paiement_service.dto.PaiementRequestDTO;
import com.fitunity.paiement_service.dto.PaiementResponseDTO;
import java.util.List;

public interface PaiementService {
    PaiementResponseDTO creerPaiement(PaiementRequestDTO request);
    PaiementResponseDTO getPaiementById(String paiementId);
    List<PaiementResponseDTO> getPaiementsByUserId(String userId);
    PaiementResponseDTO annulerPaiement(String paiementId);
    PaiementResponseDTO mettreAJourStatut(String paiementId, String statut);
}
