package com.fitunity.paiement_service.gateway;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class PayPalGateway {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    // Créer un paiement PayPal
    public Payment creerPaiement(
            BigDecimal montant,
            String currency,
            String description,
            String urlRetour,
            String urlAnnulation) throws PayPalRESTException {

        // Montant
        Amount amount = new Amount();
        amount.setCurrency(currency.toUpperCase());
        amount.setTotal(String.format("%.2f",
                montant.setScale(2, RoundingMode.HALF_UP)));

        // Transaction
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // Payeur
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // URLs de redirection
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(urlAnnulation);
        redirectUrls.setReturnUrl(urlRetour);

        // Paiement
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        // Contexte API
        APIContext apiContext = new APIContext(clientId, clientSecret, mode);

        return payment.create(apiContext);
    }

    // Confirmer un paiement PayPal
    public Payment confirmerPaiement(
            String paymentId,
            String payerId) throws PayPalRESTException {

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        APIContext apiContext = new APIContext(clientId, clientSecret, mode);

        return payment.execute(apiContext, execution);
    }
}
