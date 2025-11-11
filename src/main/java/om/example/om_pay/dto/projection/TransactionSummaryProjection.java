package om.example.om_pay.dto.projection;

import java.time.LocalDateTime;

import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;

/**
 * Projection minimale pour les résumés de transactions
 * Utilisée pour les listes légères ou les statistiques
 */
public interface TransactionSummaryProjection {
    
    Long getId();
    String getReference();
    TypeTransaction getTypeTransaction();
    Double getMontant();
    StatutTransaction getStatut();
    LocalDateTime getDateTransaction();
}
