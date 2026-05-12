package com.fitunity.paiement_service.repository;

import com.fitunity.paiement_service.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, String> {

    List<Refund> findByPaiement_PaiementId(String paiementId);
}
