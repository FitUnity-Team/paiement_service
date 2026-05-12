package com.fitunity.paiement_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void envoyerPaiementSucces(PaymentSucceededEvent event) {
        log.info("Envoi notification paiement réussi : {}", event.getPaiementId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
        log.info("Notification envoyée avec succès !");
    }
}
