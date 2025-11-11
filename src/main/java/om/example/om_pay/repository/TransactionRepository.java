package om.example.om_pay.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import om.example.om_pay.dto.projection.TransactionHistoriqueProjection;
import om.example.om_pay.dto.projection.TransactionProjection;
import om.example.om_pay.dto.projection.TransactionSummaryProjection;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.enums.TypeTransaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ========== REQUÊTES SANS PROJECTION (Entité complète) ==========
  
    Optional<Transaction> findByReference(String reference);

   
    @Query("SELECT t FROM Transaction t WHERE t.compteExpediteur.id = :compteId OR t.compteDestinataire.id = :compteId ORDER BY t.dateTransaction DESC")
    List<Transaction> findByCompteId(@Param("compteId") Long compteId);

  
    @Query("SELECT t FROM Transaction t WHERE t.distributeur.id = :distributeurId ORDER BY t.dateTransaction DESC")
    List<Transaction> findByDistributeurId(@Param("distributeurId") Long distributeurId);

   
    List<Transaction> findByTypeTransaction(TypeTransaction typeTransaction);

    /**
     * Récupère les transactions d'un compte entre deux dates
     */
    @Query("SELECT t FROM Transaction t WHERE (t.compteExpediteur.id = :compteId OR t.compteDestinataire.id = :compteId) " +
           "AND t.dateTransaction BETWEEN :dateDebut AND :dateFin ORDER BY t.dateTransaction DESC")
    List<Transaction> findByCompteIdAndDateBetween(@Param("compteId") Long compteId, 
                                                    @Param("dateDebut") LocalDateTime dateDebut,
                                                    @Param("dateFin") LocalDateTime dateFin);

    boolean existsByReference(String reference);
    
    
    // ========== REQUÊTES AVEC PROJECTIONS ==========
    
    /**
     * Projection complète : Récupère toutes les transactions d'un compte avec relations
     */
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.compteExpediteur ce " +
           "LEFT JOIN FETCH t.compteDestinataire cd " +
           "LEFT JOIN FETCH ce.utilisateur " +
           "LEFT JOIN FETCH cd.utilisateur " +
           "WHERE t.compteExpediteur.id = :compteId OR t.compteDestinataire.id = :compteId " +
           "ORDER BY t.dateTransaction DESC")
    List<TransactionProjection> findProjectionByCompteId(@Param("compteId") Long compteId);
    
    /**
     * Projection historique : Récupère l'historique optimisé d'un compte entre deux dates
     */
    @Query("SELECT t.reference as reference, " +
           "t.typeTransaction as typeTransaction, " +
           "t.montant as montant, " +
           "t.frais as frais, " +
           "t.montantTotal as montantTotal, " +
           "t.statut as statut, " +
           "t.dateTransaction as dateTransaction, " +
           "ce.numeroCompte as numeroCompteExpediteur, " +
           "cd.numeroCompte as numeroCompteDestinataire, " +
           "CONCAT(ue.prenom, ' ', ue.nom) as nomExpediteur, " +
           "ue.telephone as telephoneExpediteur, " +
           "CONCAT(ud.prenom, ' ', ud.nom) as nomDestinataire, " +
           "ud.telephone as telephoneDestinataire " +
           "FROM Transaction t " +
           "LEFT JOIN t.compteExpediteur ce " +
           "LEFT JOIN t.compteDestinataire cd " +
           "LEFT JOIN ce.utilisateur ue " +
           "LEFT JOIN cd.utilisateur ud " +
           "WHERE (t.compteExpediteur.id = :compteId OR t.compteDestinataire.id = :compteId) " +
           "AND t.dateTransaction BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY t.dateTransaction DESC")
    List<TransactionHistoriqueProjection> findHistoriqueProjection(
        @Param("compteId") Long compteId,
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Projection résumé : Liste légère des transactions d'un compte
     */
    @Query("SELECT t.id as id, " +
           "t.reference as reference, " +
           "t.typeTransaction as typeTransaction, " +
           "t.montant as montant, " +
           "t.statut as statut, " +
           "t.dateTransaction as dateTransaction " +
           "FROM Transaction t " +
           "WHERE t.compteExpediteur.id = :compteId OR t.compteDestinataire.id = :compteId " +
           "ORDER BY t.dateTransaction DESC")
    List<TransactionSummaryProjection> findSummaryByCompteId(@Param("compteId") Long compteId);
    
    /**
     * Projection résumé : Transactions d'un distributeur
     */
    @Query("SELECT t.id as id, " +
           "t.reference as reference, " +
           "t.typeTransaction as typeTransaction, " +
           "t.montant as montant, " +
           "t.statut as statut, " +
           "t.dateTransaction as dateTransaction " +
           "FROM Transaction t " +
           "WHERE t.distributeur.id = :distributeurId " +
           "ORDER BY t.dateTransaction DESC")
    List<TransactionSummaryProjection> findSummaryByDistributeurId(@Param("distributeurId") Long distributeurId);
    
    /**
     * Projection résumé : Transactions par type
     */
    @Query("SELECT t.id as id, " +
           "t.reference as reference, " +
           "t.typeTransaction as typeTransaction, " +
           "t.montant as montant, " +
           "t.statut as statut, " +
           "t.dateTransaction as dateTransaction " +
           "FROM Transaction t " +
           "WHERE t.typeTransaction = :typeTransaction " +
           "ORDER BY t.dateTransaction DESC")
    List<TransactionSummaryProjection> findSummaryByType(@Param("typeTransaction") TypeTransaction typeTransaction);
}
