package com.fitunity.paiement_service.service;

import com.fitunity.paiement_service.dto.RefundRequestDTO;
import com.fitunity.paiement_service.dto.RefundResponseDTO;
import com.fitunity.paiement_service.entity.Paiement;
import com.fitunity.paiement_service.entity.Refund;
import com.fitunity.paiement_service.enums.PaiementStatus;
import com.fitunity.paiement_service.exception.InvalidPaiementStateException;
import com.fitunity.paiement_service.exception.PaiementNotFoundException;
import com.fitunity.paiement_service.repository.PaiementRepository;
import com.fitunity.paiement_service.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final PaiementRepository paiementRepository;

    @Override
    public RefundResponseDTO creerRefund(RefundRequestDTO request) {
        Paiement paiement = paiementRepository.findById(request.getPaiementId())
                .orElseThrow(() -> new PaiementNotFoundException(
                        "Paiement introuvable avec l'ID : " + request.getPaiementId()));

        if (paiement.getStatus() != PaiementStatus.SUCCESS) {
            throw new InvalidPaiementStateException(
                    "Seuls les paiements SUCCESS peuvent être remboursés. Statut actuel : "
                            + paiement.getStatus());
        }

        Refund refund = Refund.builder()
                .paiement(paiement)
                .amount(request.getAmount())
                .reason(request.getReason())
                .totalAmount(paiement.getAmount())
                .build();

        paiement.setStatus(PaiementStatus.REFUNDED);
        paiementRepository.save(paiement);

        return mapToResponse(refundRepository.save(refund));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponseDTO> getRefundsByPaiementId(String paiementId) {
        return refundRepository.findByPaiement_PaiementId(paiementId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RefundResponseDTO mapToResponse(Refund refund) {
        return RefundResponseDTO.builder()
                .refundId(refund.getRefundId())
                .paiementId(refund.getPaiement().getPaiementId())
                .amount(refund.getAmount())
                .reason(refund.getReason())
                .createdAt(refund.getCreatedAt())
                .totalAmount(refund.getTotalAmount())
                .build();
    }
}