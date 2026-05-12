package com.fitunity.paiement_service.exception;

public class PaiementNotFoundException extends RuntimeException {
    public PaiementNotFoundException(String message) {
        super(message);
    }
}