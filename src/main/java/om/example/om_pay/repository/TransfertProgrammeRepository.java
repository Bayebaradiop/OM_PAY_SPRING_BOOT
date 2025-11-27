package om.example.om_pay.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import om.example.om_pay.model.TransfertProgramme;
import om.example.om_pay.model.enums.StatutTransfertProgramme;

@Repository
public interface TransfertProgrammeRepository extends JpaRepository<TransfertProgramme, Long> {
    
    List<TransfertProgramme> findByStatutAndDateExecutionBefore(
        StatutTransfertProgramme statut, 
        LocalDateTime maintenant
    );
    
    List<TransfertProgramme> findByUtilisateurExpediteur_IdOrderByDateCreationDesc(Long utilisateurId);
}
