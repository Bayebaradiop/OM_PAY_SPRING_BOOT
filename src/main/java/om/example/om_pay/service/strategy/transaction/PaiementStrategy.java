package om.example.om_pay.service.strategy.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.PaiementRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Marchand;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.MarchandRepository;
import om.example.om_pay.repository.TransactionRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.QRCodeResolverService;
import om.example.om_pay.service.baseservice.BaseTransactionService;
import om.example.om_pay.service.calculation.FraisCalculService;
import om.example.om_pay.service.calculation.PlafondService;
import om.example.om_pay.utils.ReferenceGeneratorService;
import om.example.om_pay.validations.CompteValidationService;

/**
 * Strategy pour les paiements effectués chez un marchand.
 * 
 * Spécificités :
 * - Frais de 0.85% du montant
 * - Uniquement débit du client (pas de crédit sur compte marchand)
 * - Identification du marchand par son code
 * - Enregistrement du marchand dans la transaction
 */
@Service
public class PaiementStrategy extends BaseTransactionService {
    
    private final MarchandRepository marchandRepository;
    private final QRCodeResolverService qrCodeResolverService;
    
    public PaiementStrategy(
            TransactionRepository transactionRepository,
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository,
            CompteValidationService compteValidationService,
            FraisCalculService fraisCalculService,
            PlafondService plafondService,
            ReferenceGeneratorService referenceGeneratorService,
            MarchandRepository marchandRepository,
            QRCodeResolverService qrCodeResolverService) {
        super(transactionRepository, compteRepository, utilisateurRepository,
              compteValidationService, fraisCalculService, plafondService,
              referenceGeneratorService);
        this.marchandRepository = marchandRepository;
        this.qrCodeResolverService = qrCodeResolverService;
    }
    
    @Override
    public TypeTransaction getType() {
        return TypeTransaction.PAIEMENT;
    }
    
    @Override
    @Transactional
    public TransactionResponse executer(Object requestObj) {
        PaiementRequest request = (PaiementRequest) requestObj;
        
        // 1. Récupérer l'utilisateur connecté (client)
        Utilisateur client = getUtilisateurConnecte();
        
        // 2. Récupérer le compte du client
        Compte compteClient = compteValidationService.getComptePrincipal(client);
        
        // 3. Récupérer le marchand par code ou QR code
        Marchand marchand;
        boolean isQRUsed = false;
        if (request.getCodeQr() != null && !request.getCodeQr().isBlank()) {
            // Résoudre le QR code vers un compte, puis récupérer le marchand par téléphone
            Compte compteMarchand = qrCodeResolverService.resoudreQRCodeVersCompte(request.getCodeQr());
            Utilisateur utilisateurMarchand = compteMarchand.getUtilisateur();
            marchand = marchandRepository.findByNumeroMarchand(utilisateurMarchand.getTelephone())
                    .orElseThrow(() -> new ResourceNotFoundException("Marchand non trouvé pour ce QR code"));
            isQRUsed = true;
        } else {
            marchand = marchandRepository.findByCodeMarchand(request.getCodeMarchand())
                    .orElseThrow(() -> new ResourceNotFoundException("Marchand non trouvé"));
        }
        
        // 4. Calculer les frais et montant total
        FraisCalculService.FraisResult fraisResult = fraisCalculService.calculerFraisEtTotal(
            TypeTransaction.PAIEMENT, request.getMontant()
        );
        
        // 5. Vérifier le solde
        compteValidationService.verifierSolde(compteClient, fraisResult.getMontantTotal());
        
        // 6. Effectuer le paiement (débiter le client uniquement)
        compteClient.setSolde(compteClient.getSolde() - fraisResult.getMontantTotal());
        compteRepository.save(compteClient);
        
        // 7. Créer et sauvegarder la transaction
        Transaction transaction = creerTransaction(
            TypeTransaction.PAIEMENT,
            compteClient,
            null, // Pas de compte destinataire pour un paiement marchand
            request.getMontant(),
            fraisResult.getFrais(),
            fraisResult.getMontantTotal(),
            null,
            marchand
        );
        
        // 8. Incrémenter le compteur d'utilisation du QR code si utilisé
        if (isQRUsed) {
            qrCodeResolverService.incrementerUtilisation(request.getCodeQr());
        }
        
        return TransactionResponse.fromTransaction(transaction);
    }
}
