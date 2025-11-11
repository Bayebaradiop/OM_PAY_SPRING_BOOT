package om.example.om_pay.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeCompte;

/**
 * DTO pour la réponse du profil complet de l'utilisateur
 * Contient toutes les informations : utilisateur, compte, statistiques, transactions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilCompletResponse {
    
    // ========== Informations utilisateur ==========
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private Role role;
    private Statut statut;
    private Boolean premiereConnexion;
    private LocalDateTime dateCreation;
    
    // ========== Informations du compte ==========
    private CompteInfo compte;
    
    // ========== Statistiques ==========
    private StatistiquesUtilisateur statistiques;
    
    // ========== Dernières transactions ==========
    private List<TransactionResume> dernieresTransactions;
    
    /**
     * Informations du compte principal
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompteInfo {
        private Long id;
        private String numeroCompte;
        private Double solde;
        private TypeCompte typeCompte;
        private Statut statut;
        private LocalDateTime dateCreation;
    }
    
    /**
     * Statistiques de l'utilisateur
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatistiquesUtilisateur {
        private Long nombreTransactions;
        private Long nombreTransactionsReussies;
        private Long nombreTransactionsEchouees;
        private Double totalEnvoye;
        private Double totalRecu;
        private Double plafondQuotidien;
        private Double totalTransfertJour;
        private Double plafondRestant;
    }
    
    /**
     * Résumé d'une transaction
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResume {
        private Long id;
        private String reference;
        private String typeTransaction;
        private Double montant;
        private Double frais;
        private String statut;
        private String compteExpediteur;
        private String compteDestinataire;
        private LocalDateTime dateCreation;
    }
}
