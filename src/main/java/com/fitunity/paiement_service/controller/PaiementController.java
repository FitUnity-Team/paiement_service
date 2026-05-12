package com.fitunity.paiement_service.controller;
import com.fitunity.paiement_service.repository.PaiementRepository;
import com.fitunity.paiement_service.entity.Paiement;
import com.fitunity.paiement_service.exception.PaiementNotFoundException;
import com.fitunity.paiement_service.pdf.FacturePdfService;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fitunity.paiement_service.dto.PaiementRequestDTO;
import com.fitunity.paiement_service.dto.PaiementResponseDTO;
import com.fitunity.paiement_service.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;
    private final FacturePdfService facturePdfService;
    private final PaiementRepository paiementRepository;

    @PostMapping
    public ResponseEntity<PaiementResponseDTO> creerPaiement(
            @Valid @RequestBody PaiementRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paiementService.creerPaiement(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementResponseDTO> getPaiementById(@PathVariable String id) {
        return ResponseEntity.ok(paiementService.getPaiementById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaiementResponseDTO>> getHistorique(
            @PathVariable String userId) {
        return ResponseEntity.ok(paiementService.getPaiementsByUserId(userId));
    }

    @GetMapping("/{id}/statut")
    public ResponseEntity<PaiementResponseDTO> getStatut(@PathVariable String id) {
        return ResponseEntity.ok(paiementService.getPaiementById(id));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<PaiementResponseDTO> annulerPaiement(@PathVariable String id) {
        return ResponseEntity.ok(paiementService.annulerPaiement(id));
    }
    // GET /api/paiements/paypal/success
    @GetMapping("/paypal/success")
    public ResponseEntity<String> paypalSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        return ResponseEntity.ok("Paiement PayPal confirmé !");
    }

    // GET /api/paiements/paypal/cancel
    @GetMapping("/paypal/cancel")
    public ResponseEntity<String> paypalCancel() {
        return ResponseEntity.ok("Paiement PayPal annulé !");
    }
    // GET /api/paiements/{id}/facture → Télécharger la facture PDF
    @GetMapping("/{id}/facture")
    public ResponseEntity<byte[]> telechargerFacture(@PathVariable String id) {
        try {
            // Récupérer le paiement
            Paiement paiement = paiementRepository.findById(id)
                    .orElseThrow(() -> new PaiementNotFoundException(
                            "Paiement introuvable avec l'ID : " + id));

            // Générer le PDF
            byte[] pdf = facturePdfService.genererFacture(paiement);

            // Headers pour téléchargement
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "facture-" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}