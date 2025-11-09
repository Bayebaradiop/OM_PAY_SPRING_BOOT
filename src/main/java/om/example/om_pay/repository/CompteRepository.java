package om.example.om_pay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import om.example.om_pay.model.Compte;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

    Optional<Compte> findByNumeroCompte(String numeroCompte);

  
    boolean existsByNumeroCompte(String numeroCompte);

    List<Compte> findByUtilisateurId(Long utilisateurId);
}
