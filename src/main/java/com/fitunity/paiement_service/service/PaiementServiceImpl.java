package com.fitunity.paiement_service.service;

import com.fitunity.paiement_service.dto.PaiementRequestDTO;
import com.fitunity.paiement_service.dto.PaiementResponseDTO;
import com.fitunity.paiement_service.entity.Paiement;
import com.fitunity.paiement_service.enums.PaiementMethod;
import com.fitunity.paiement_service.enums.PaiementStatus;
import com.fitunity.paiement_service.exception.InvalidPaiementStateException;
import com.fitunity.paiement_service.exception.PaiementNotFoundException;
import com.fitunity.paiement_service.gateway.StripeGateway;
import com.fitunity.paiement_service.gateway.PayPalGateway;
import com.fitunity.paiement_service.messaging.NotificationProducer;
import com.fitunity.paiement_service.messaging.PaymentSucceededEvent;
import com.fitunity.paiement_service.repository.PaiementRepository;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final StripeGateway stripeGateway;
    private final PayPalGateway payPalGateway;
    private final NotificationProducer notificationProducer;

    @Override
    public PaiementResponseDTO creerPaiement(PaiementRequestDTO request) {
        Paiement paiement = Paiement.builder()
                .userId(request.getUserId())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .amount(request.getAmount())
                .paiementMethod(request.getPaiementMethod())
                .currency(request.getCurrency())
                .status(PaiementStatus.PENDING)
                .build();

        if (request.getPaiementMethod() == PaiementMethod.CARD) {
            try {
                PaymentIntent paymentIntent = stripeGateway.creerPaiement(
                        request.getAmount(), request.getCurrency());
                paiement.setStripePaymentIntentId(paymentIntent.getId());
                paiement.setClientSecret(paymentIntent.getClientSecret());
                paiement.setStatus(PaiementStatus.PENDING);
            } catch (Exception e) {
                paiement.setStatus(PaiementStatus.FAILED);
            }
        } else if (request.getPaiementMethod() == PaiementMethod.PAYPAL) {
            try {
                com.paypal.api.payments.Payment paypalPayment =
                        payPalGateway.creerPaiement(
                                request.getAmount(),
                                request.getCurrency(),
                                "Paiement FitUnity",
                                "http://localhost:8083/api/paiements/paypal/success",
                                "http://localhost:8083/api/paiements/paypal/cancel"
                        );

                String approvalUrl = paypalPayment.getLinks().stream()
                        .filter(link -> "approval_url".equals(link.getRel()))
                        .findFirst()
                        .map(link -> link.getHref())
                        .orElse(null);

                paiement.setPaypalPaymentId(paypalPayment.getId());
                paiement.setPaypalApprovalUrl(approvalUrl);
                paiement.setStatus(PaiementStatus.PENDING);
            } catch (Exception e) {
                paiement.setStatus(PaiementStatus.FAILED);
            }
        }

        return mapToResponse(paiementRepository.save(paiement));
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementResponseDTO getPaiementById(String paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaiementNotFoundException(
                        "Paiement introuvable avec l'ID : " + paiementId));
        return mapToResponse(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponseDTO> getPaiementsByUserId(String userId) {
        return paiementRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaiementResponseDTO annulerPaiement(String paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaiementNotFoundException(
                        "Paiement introuvable avec l'ID : " + paiementId));

        if (paiement.getStatus() != PaiementStatus.PENDING) {
            throw new InvalidPaiementStateException(
                    "Seuls les paiements PENDING peuvent être annulés. Statut actuel : "
                            + paiement.getStatus());
        }

        paiement.setStatus(PaiementStatus.CANCELLED);
        return mapToResponse(paiementRepository.save(paiement));
    }

    @Override
    public PaiementResponseDTO mettreAJourStatut(String paiementId, String statut) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new PaiementNotFoundException(
                        "Paiement introuvable avec l'ID : " + paiementId));

        PaiementStatus newStatus = PaiementStatus.valueOf(statut.toUpperCase());
        paiement.setStatus(newStatus);
        Paiement saved = paiementRepository.save(paiement);

        // Envoyer notification si paiement SUCCESS
        if (newStatus == PaiementStatus.SUCCESS) {
            PaymentSucceededEvent event = PaymentSucceededEvent.builder()
                    .paiementId(saved.getPaiementId())
                    .userId(saved.getUserId())
                    .referenceId(saved.getReferenceId())
                    .referenceType(saved.getReferenceType())
                    .amount(saved.getAmount())
                    .currency(saved.getCurrency())
                    .paiementMethod(saved.getPaiementMethod().toString())
                    .paiementDate(saved.getPaiementDate())
                    .build();

            notificationProducer.envoyerPaiementSucces(event);
        }

        return mapToResponse(saved);
    }

    private PaiementResponseDTO mapToResponse(Paiement paiement) {
        return PaiementResponseDTO.builder()
                .paiementId(paiement.getPaiementId())
                .userId(paiement.getUserId())
                .referenceId(paiement.getReferenceId())
                .referenceType(paiement.getReferenceType())
                .amount(paiement.getAmount())
                .status(paiement.getStatus())
                .paiementDate(paiement.getPaiementDate())
                .paiementMethod(paiement.getPaiementMethod())
                .currency(paiement.getCurrency())
                .stripePaymentIntentId(paiement.getStripePaymentIntentId())
                .clientSecret(paiement.getClientSecret())
                .paypalPaymentId(paiement.getPaypalPaymentId())
                .paypalApprovalUrl(paiement.getPaypalApprovalUrl())
                .build();
    }
}