package om.example.om_pay.dto.projection;

import java.time.LocalDateTime;

import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;

/**
 * Projection optimisée pour l'historique des transactions
 * Retourne uniquement les champs essentiels pour l'affichage
 */
public interface TransactionHistoriqueProjection {
    
    String getReference();
    TypeTransaction getTypeTransaction();
    Double getMontant();
    Double getFrais();
    Double getMontantTotal();
    StatutTransaction getStatut();
    LocalDateTime getDateTransaction();
    
    // Informations simplifiées des comptes
    String getNumeroCompteExpediteur();
    String getNumeroCompteDestinataire();
    
    // Informations simplifiées des utilisateurs
    String getNomExpediteur();
    String getTelephoneExpediteur();
    String getNomDestinataire();
    String getTelephoneDestinataire();
}
