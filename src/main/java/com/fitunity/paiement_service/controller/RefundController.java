package com.fitunity.paiement_service.controller;

import com.fitunity.paiement_service.dto.RefundRequestDTO;
import com.fitunity.paiement_service.dto.RefundResponseDTO;
import com.fitunity.paiement_service.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<RefundResponseDTO> creerRefund(
            @Valid @RequestBody RefundRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(refundService.creerRefund(request));
    }

    @GetMapping("/paiement/{paiementId}")
    public ResponseEntity<List<RefundResponseDTO>> getRefunds(
            @PathVariable String paiementId) {
        return ResponseEntity.ok(refundService.getRefundsByPaiementId(paiementId));
    }
}