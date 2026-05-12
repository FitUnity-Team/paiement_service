package com.fitunity.paiement_service.exception;

public class InvalidPaiementStateException extends RuntimeException {
    public InvalidPaiementStateException(String message) {
        super(message);
    }
}