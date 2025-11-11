package om.example.om_pay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import om.example.om_pay.model.Marchand;

@Repository
public interface MarchandRepository extends JpaRepository<Marchand, Long> {

 
    Optional<Marchand> findByCodeMarchand(String codeMarchand);

    boolean existsByCodeMarchand(String codeMarchand);

  
    Optional<Marchand> findByNomCommercial(String nomCommercial);
    
    Optional<Marchand> findByNumeroMarchand(String numeroMarchand);
}
