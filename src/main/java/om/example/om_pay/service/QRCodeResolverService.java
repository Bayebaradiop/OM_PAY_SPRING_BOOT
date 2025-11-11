package om.example.om_pay.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.QRCode;
import om.example.om_pay.repository.QRCodeRepository;

/**
 * Service utilitaire pour résoudre un QR code en compte
 */
@Service
@RequiredArgsConstructor
public class QRCodeResolverService {

    private final QRCodeRepository qrCodeRepository;

    /**
     * Résout un code QR vers le compte associé
     */
    public Compte resoudreQRCodeVersCompte(String codeQr) {
        if (codeQr == null || codeQr.isBlank()) {
            throw new BadRequestException("Le code QR ne peut pas être vide");
        }
        
        QRCode qrCode = qrCodeRepository
            .findByCodeQrAndActif(codeQr, true)
            .orElseThrow(() -> new ResourceNotFoundException("QR code invalide ou inactif"));
        
        return qrCode.getCompte();
    }
    
    /**
     * Met à jour les statistiques d'utilisation du QR code
     */
    public void incrementerUtilisation(String codeQr) {
        qrCodeRepository.findByCodeQrAndActif(codeQr, true)
            .ifPresent(qr -> {
                qr.setNombreUtilisations(qr.getNombreUtilisations() + 1);
                qr.setDateUtilisation(LocalDateTime.now());
                qrCodeRepository.save(qr);
            });
    }
}
