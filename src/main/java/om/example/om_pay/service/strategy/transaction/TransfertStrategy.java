package om.example.om_pay.service.strategy.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.TransfertRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.TransactionRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.QRCodeResolverService;
import om.example.om_pay.service.baseservice.BaseTransactionService;
import om.example.om_pay.service.calculation.FraisCalculService;
import om.example.om_pay.service.calculation.PlafondService;
import om.example.om_pay.utils.ReferenceGeneratorService;
import om.example.om_pay.validations.CompteValidationService;

/**
 * Strategy pour les transferts d'argent entre utilisateurs.
 * 
 * Spécificités :
 * - Frais de 0.85% du montant
 * - Vérification du plafond quotidien
 * - Interdiction des auto-transferts
 * - Mise à jour du totalTransfertJour
 */
@Service
public class TransfertStrategy extends BaseTransactionService {
    
    private final QRCodeResolverService qrCodeResolverService;
    
    public TransfertStrategy(
            TransactionRepository transactionRepository,
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository,
            CompteValidationService compteValidationService,
            FraisCalculService fraisCalculService,
            PlafondService plafondService,
            ReferenceGeneratorService referenceGeneratorService,
            QRCodeResolverService qrCodeResolverService) {
        super(transactionRepository, compteRepository, utilisateurRepository,
              compteValidationService, fraisCalculService, plafondService,
              referenceGeneratorService);
        this.qrCodeResolverService = qrCodeResolverService;
    }
    
    @Override
    public TypeTransaction getType() {
        return TypeTransaction.TRANSFERT;
    }
    
    @Override
    @Transactional
    public TransactionResponse executer(Object requestObj) {
        TransfertRequest request = (TransfertRequest) requestObj;
        
        // 1. Récupérer l'utilisateur connecté (expéditeur)
        Utilisateur expediteur = getUtilisateurConnecte();
        
        // 2. Récupérer et valider les comptes
        Compte compteExpediteur = compteValidationService.getComptePrincipal(expediteur);
        
        // Résoudre le compte destinataire via QR code ou téléphone
        Compte compteDestinataire;
        boolean isQRUsed = false;
        if (request.getCodeQrDestinataire() != null && !request.getCodeQrDestinataire().isBlank()) {
            compteDestinataire = qrCodeResolverService.resoudreQRCodeVersCompte(request.getCodeQrDestinataire());
            isQRUsed = true;
        } else {
            compteDestinataire = compteValidationService.getCompteByTelephone(
                request.getTelephoneDestinataire()
            );
        }
        compteValidationService.verifierPasAutoTransfert(compteExpediteur, compteDestinataire);
        
        // 3. Calculer les frais et le montant total
        FraisCalculService.FraisResult fraisResult = fraisCalculService.calculerFraisEtTotal(
            TypeTransaction.TRANSFERT, request.getMontant()
        );
        
        // 4. Vérifications (solde + plafond)
        compteValidationService.verifierSolde(compteExpediteur, fraisResult.getMontantTotal());
        plafondService.verifierPlafond(expediteur, request.getMontant());
        
        // 5. Effectuer le transfert
        effectuerTransfertComptes(
            compteExpediteur, 
            compteDestinataire, 
            fraisResult.getMontantTotal(), 
            request.getMontant()
        );
        
        // 6. Mettre à jour le plafond quotidien
        plafondService.incrementerTransfertJour(expediteur, request.getMontant());
        
        // 7. Créer et sauvegarder la transaction
        Transaction transaction = creerTransaction(
            TypeTransaction.TRANSFERT,
            compteExpediteur,
            compteDestinataire,
            request.getMontant(),
            fraisResult.getFrais(),
            fraisResult.getMontantTotal(),
            null,
            null
        );
        
        // 8. Incrémenter le compteur d'utilisation du QR code si utilisé
        if (isQRUsed) {
            qrCodeResolverService.incrementerUtilisation(request.getCodeQrDestinataire());
        }
        
        return TransactionResponse.fromTransaction(transaction);
    }
}
