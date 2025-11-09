package om.example.om_pay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import om.example.om_pay.model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

  
    Optional<Utilisateur> findByTelephone(String telephone);

    boolean existsByTelephone(String telephone);

    
    boolean existsByEmail(String email);
}
