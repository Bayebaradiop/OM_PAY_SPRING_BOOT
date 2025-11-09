package om.example.om_pay.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.enums.TypeTransaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  
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
}
