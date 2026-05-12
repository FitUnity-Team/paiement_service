package com.fitunity.paiement_service.service;

import com.fitunity.paiement_service.dto.RefundRequestDTO;
import com.fitunity.paiement_service.dto.RefundResponseDTO;
import java.util.List;

public interface RefundService {
    RefundResponseDTO creerRefund(RefundRequestDTO request);
    List<RefundResponseDTO> getRefundsByPaiementId(String paiementId);
}