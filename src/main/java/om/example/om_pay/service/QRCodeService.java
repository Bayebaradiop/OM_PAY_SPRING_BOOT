package om.example.om_pay.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.RequiredArgsConstructor;
import om.example.om_pay.dto.request.ScanQRRequest;
import om.example.om_pay.dto.response.QRCodeResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.QRCode;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.TypeCompte;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.QRCodeRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.baseservice.BaseTransactionService;

/**
 * Service pour gérer les QR codes
 */
@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CompteRepository compteRepository;
    private final BaseTransactionService transactionService;

    /**
     * Récupérer le QR code de l'utilisateur connecté
     */
    public QRCodeResponse getMonQRCode() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        
        Compte compte = compteRepository
            .findByUtilisateurAndTypeCompte(utilisateur, TypeCompte.PRINCIPAL)
            .orElseThrow(() -> new ResourceNotFoundException("Compte principal non trouvé"));
        
        QRCode qrCode = qrCodeRepository.findByCompte(compte)
            .orElseThrow(() -> new ResourceNotFoundException("QR code non trouvé"));
        
        return convertirEnResponse(qrCode);
    }

    /**
     * Scanner un QR code et effectuer le transfert
     */
    @Transactional
    public void scannerEtPayer(ScanQRRequest request) {
        // Récupérer le QR code
        QRCode qrCode = qrCodeRepository
            .findByCodeQrAndActif(request.getCodeQr(), true)
            .orElseThrow(() -> new BadRequestException("QR code invalide ou inactif"));
        
        // Récupérer l'utilisateur qui paie (scanner)
        Utilisateur payeur = getUtilisateurConnecte();
        Compte comptePayeur = compteRepository
            .findByUtilisateurAndTypeCompte(payeur, TypeCompte.PRINCIPAL)
            .orElseThrow(() -> new ResourceNotFoundException("Compte payeur non trouvé"));
        
        // Vérifier qu'on ne paie pas soi-même
        if (comptePayeur.getId().equals(qrCode.getCompte().getId())) {
            throw new BadRequestException("Vous ne pouvez pas scanner votre propre QR code");
        }
        
        // Vérifier le solde
        if (comptePayeur.getSolde() < request.getMontant()) {
            throw new BadRequestException("Solde insuffisant");
        }
        
        // Effectuer le transfert
        comptePayeur.setSolde(comptePayeur.getSolde() - request.getMontant());
        qrCode.getCompte().setSolde(qrCode.getCompte().getSolde() + request.getMontant());
        
        compteRepository.save(comptePayeur);
        compteRepository.save(qrCode.getCompte());
        
        // Mettre à jour les stats du QR code
        qrCode.setDateUtilisation(LocalDateTime.now());
        qrCode.setNombreUtilisations(qrCode.getNombreUtilisations() + 1);
        qrCodeRepository.save(qrCode);
    }

    /**
     * Génère l'image du QR code en Base64
     */
    private String genererImageQRCode(String contenu) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                contenu, 
                BarcodeFormat.QR_CODE, 
                300, 
                300
            );
            
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }

    /**
     * Convertit un QRCode en QRCodeResponse avec l'image
     */
    private QRCodeResponse convertirEnResponse(QRCode qrCode) {
        Utilisateur utilisateur = qrCode.getCompte().getUtilisateur();
        
        return QRCodeResponse.builder()
            .id(qrCode.getId())
            .codeQr(qrCode.getCodeQr())
            .qrCodeImage(genererImageQRCode(qrCode.getCodeQr()))
            .numeroCompte(qrCode.getCompte().getNumeroCompte())
            .nomComplet(utilisateur.getPrenom() + " " + utilisateur.getNom())
            .actif(qrCode.getActif())
            .dateCreation(qrCode.getDateCreation())
            .nombreUtilisations(qrCode.getNombreUtilisations())
            .build();
    }

    private Utilisateur getUtilisateurConnecte() {
        String telephone = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByTelephone(telephone)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }
}
