package com.fitunity.paiement_service.repository;

import com.fitunity.paiement_service.entity.Paiement;
import com.fitunity.paiement_service.enums.PaiementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, String> {

    List<Paiement> findByUserId(String userId);
    List<Paiement> findByStatus(PaiementStatus status);
    List<Paiement> findByUserIdAndStatus(String userId, PaiementStatus status);
    List<Paiement> findByReferenceId(String referenceId);
}
