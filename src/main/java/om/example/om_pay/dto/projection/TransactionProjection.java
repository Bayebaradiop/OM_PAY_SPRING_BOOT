package om.example.om_pay.dto.projection;

import java.time.LocalDateTime;

import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;

/**
 * Projection complète pour les transactions
 * Inclut toutes les informations principales d'une transaction
 */
public interface TransactionProjection {
    
    Long getId();
    String getReference();
    TypeTransaction getTypeTransaction();
    Double getMontant();
    Double getFrais();
    Double getMontantTotal();
    StatutTransaction getStatut();
    LocalDateTime getDateTransaction();
    
    // Projections imbriquées pour les comptes
    CompteProjection getCompteExpediteur();
    CompteProjection getCompteDestinataire();
    
    // Projection pour distributeur (nullable)
    DistributeurProjection getDistributeur();
    
    // Projection pour marchand (nullable)
    MarchandProjection getMarchand();
    
    /**
     * Projection pour les informations du compte
     */
    interface CompteProjection {
        Long getId();
        String getNumeroCompte();
        Double getSolde();
        
        // Informations de l'utilisateur du compte
        UtilisateurProjection getUtilisateur();
    }
    
    /**
     * Projection pour les informations de l'utilisateur
     */
    interface UtilisateurProjection {
        Long getId();
        String getNom();
        String getPrenom();
        String getTelephone();
    }
    
    /**
     * Projection pour les informations du distributeur
     */
    interface DistributeurProjection {
        Long getId();
        String getNom();
        String getPrenom();
        String getTelephone();
    }
    
    /**
     * Projection pour les informations du marchand
     */
    interface MarchandProjection {
        Long getId();
        String getNomCommercial();
        String getCodeMarchand();
        String getCategorie();
    }
}
