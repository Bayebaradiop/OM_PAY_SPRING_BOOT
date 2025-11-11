package om.example.om_pay.service.strategy.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.RetraitRequest;
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
 * Strategy pour les retraits effectués par un distributeur pour un client.
 * 
 * Spécificités :
 * - Réservé aux distributeurs uniquement
 * - Aucun frais appliqué
 * - Transfert : client → distributeur
 * - Enregistrement du distributeur dans la transaction
 */
@Service
public class RetraitStrategy extends BaseTransactionService {
    
    private final QRCodeResolverService qrCodeResolverService;
    
    public RetraitStrategy(
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
        return TypeTransaction.RETRAIT;
    }
    
    @Override
    @Transactional
    public TransactionResponse executer(Object requestObj) {
        RetraitRequest request = (RetraitRequest) requestObj;
        
        // 1. Récupérer le distributeur connecté
        Utilisateur distributeur = getUtilisateurConnecte();
        
        // 2. Vérifier que l'utilisateur est bien un DISTRIBUTEUR
        verifierRoleDistributeur(distributeur);
        
        // 3. Récupérer et valider les comptes
        // Résoudre le compte client via QR code ou téléphone
        Compte compteClient;
        boolean isQRUsed = false;
        if (request.getCodeQr() != null && !request.getCodeQr().isBlank()) {
            compteClient = qrCodeResolverService.resoudreQRCodeVersCompte(request.getCodeQr());
            isQRUsed = true;
        } else {
            compteClient = compteValidationService.getCompteByTelephone(
                request.getTelephoneClient()
            );
        }
        Compte compteDistributeur = compteValidationService.getComptePrincipal(distributeur);
        
        // 4. Vérifier le solde du client (pas de frais pour retrait)
        compteValidationService.verifierSolde(compteClient, request.getMontant());
        
        // 5. Effectuer le retrait (seul le client perd de l'argent, le distributeur donne du liquide)
        debiterCompte(compteClient, request.getMontant());
        
        // 6. Créer et sauvegarder la transaction
        Transaction transaction = creerTransaction(
            TypeTransaction.RETRAIT,
            compteClient,
            compteDistributeur,
            request.getMontant(),
            0.0,
            request.getMontant(),
            distributeur,
            null
        );
        
        // 7. Incrémenter le compteur d'utilisation du QR code si utilisé
        if (isQRUsed) {
            qrCodeResolverService.incrementerUtilisation(request.getCodeQr());
        }
        
        return TransactionResponse.fromTransaction(transaction);
    }
}
