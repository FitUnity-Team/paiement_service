package com.fitunity.paiement_service.entity;

import com.fitunity.paiement_service.enums.PaiementMethod;
import com.fitunity.paiement_service.enums.PaiementStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "paiement_id")
    private String paiementId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Column(name = "reference_type", nullable = false)
    private String referenceType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaiementStatus status;

    @Column(name = "paiement_date")
    private LocalDateTime paiementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "paiement_method", nullable = false)
    private PaiementMethod paiementMethod;

    @Column(nullable = false, length = 3)
    private String currency;
    // Stripe
    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "client_secret")
    private String clientSecret;
    // PayPal
    @Column(name = "paypal_payment_id")
    private String paypalPaymentId;

    @Column(name = "paypal_approval_url")
    private String paypalApprovalUrl;

    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Refund> refunds;

    @PrePersist
    protected void onCreate() {
        if (this.paiementDate == null) {
            this.paiementDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = PaiementStatus.PENDING;
        }
    }
}
