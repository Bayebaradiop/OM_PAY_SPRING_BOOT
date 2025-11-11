package om.example.om_pay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import om.example.om_pay.model.Compte;
import om.example.om_pay.model.QRCode;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    
    Optional<QRCode> findByCodeQrAndActif(String codeQr, Boolean actif);
    
    Optional<QRCode> findByCompte(Compte compte);
    
    boolean existsByCodeQr(String codeQr);
}
