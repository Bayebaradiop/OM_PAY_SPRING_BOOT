package om.example.om_pay.listener;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import om.example.om_pay.event.CompteCreationEvent;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.QRCode;
import om.example.om_pay.repository.QRCodeRepository;

/**
 * Écoute l'événement de création de compte et génère automatiquement un QR code
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QRCodeGenerationListener {

    private final QRCodeRepository qrCodeRepository;

    @EventListener
    @Async
    @Transactional
    public void handleCompteCreation(CompteCreationEvent event) {
        try {
            Compte compte = event.getCompte();
            
            log.info("Génération automatique du QR code pour le compte: {}", 
                     compte.getNumeroCompte());
            
            // Générer un code QR unique
            String codeQr = genererCodeUnique();
            
            // Créer le QR code
            QRCode qrCode = new QRCode();
            qrCode.setCodeQr(codeQr);
            qrCode.setCompte(compte);
            qrCode.setActif(true);
            
            qrCodeRepository.save(qrCode);
            
            log.info("QR code généré avec succès: {} pour le compte: {}", 
                     codeQr, compte.getNumeroCompte());
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code pour le compte: {}", 
                      event.getCompte().getNumeroCompte(), e);
        }
    }
    
    private String genererCodeUnique() {
        String code;
        do {
            // Format: QR + 16 caractères alphanumériques
            code = "QR" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
        } while (qrCodeRepository.existsByCodeQr(code));
        
        return code;
    }
}
