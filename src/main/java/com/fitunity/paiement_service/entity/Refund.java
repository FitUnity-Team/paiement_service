package com.fitunity.paiement_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refund_id")
    private String refundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id", nullable = false)
    private Paiement paiement;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
