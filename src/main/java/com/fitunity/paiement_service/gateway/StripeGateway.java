package com.fitunity.paiement_service.gateway;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class StripeGateway {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    // Créer un paiement Stripe
    public PaymentIntent creerPaiement(BigDecimal amount, String currency)
            throws StripeException {

        // Stripe travaille en centimes → multiplier par 100
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase()) // "mad", "eur", "usd"
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    // Confirmer un paiement Stripe
    public PaymentIntent confirmerPaiement(String paymentIntentId)
            throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent;
    }

    // Rembourser un paiement Stripe
    public void rembourserPaiement(String paymentIntentId)
            throws StripeException {
        com.stripe.model.Refund.create(
                com.stripe.param.RefundCreateParams.builder()
                        .setPaymentIntent(paymentIntentId)
                        .build()
        );
    }
}
