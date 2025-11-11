package om.example.om_pay.service.strategy.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.DepotRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.TransactionRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.baseservice.BaseTransactionService;
import om.example.om_pay.service.calculation.FraisCalculService;
import om.example.om_pay.service.calculation.PlafondService;
import om.example.om_pay.utils.ReferenceGeneratorService;
import om.example.om_pay.validations.CompteValidationService;

/**
 * Strategy pour les dépôts effectués par un distributeur vers un client.
 * 
 * Spécificités :
 * - Réservé aux distributeurs uniquement
 * - Aucun frais appliqué
 * - Transfert : distributeur → client
 * - Enregistrement du distributeur dans la transaction
 */
@Service
public class DepotStrategy extends BaseTransactionService {
    
    public DepotStrategy(
            TransactionRepository transactionRepository,
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository,
            CompteValidationService compteValidationService,
            FraisCalculService fraisCalculService,
            PlafondService plafondService,
            ReferenceGeneratorService referenceGeneratorService) {
        super(transactionRepository, compteRepository, utilisateurRepository,
              compteValidationService, fraisCalculService, plafondService,
              referenceGeneratorService);
    }
    
    @Override
    public TypeTransaction getType() {
        return TypeTransaction.DEPOT;
    }
    
    @Override
    @Transactional
    public TransactionResponse executer(Object requestObj) {
        DepotRequest request = (DepotRequest) requestObj;
        
        // 1. Récupérer le distributeur connecté
        Utilisateur distributeur = getUtilisateurConnecte();
        
        // 2. Vérifier que l'utilisateur est bien un DISTRIBUTEUR
        verifierRoleDistributeur(distributeur);
        
        // 3. Récupérer et valider les comptes
        Compte compteDistributeur = compteValidationService.getComptePrincipal(distributeur);
        Compte compteClient = compteValidationService.getCompteByTelephone(
            request.getTelephoneClient()
        );
        
        // 4. Vérifier le solde du distributeur (pas de frais pour dépôt)
        compteValidationService.verifierSolde(compteDistributeur, request.getMontant());
        
        // 5. Effectuer le dépôt
        effectuerTransfertComptes(
            compteDistributeur,
            compteClient,
            request.getMontant(),
            request.getMontant()
        );
        
        // 6. Créer et sauvegarder la transaction
        Transaction transaction = creerTransaction(
            TypeTransaction.DEPOT,
            compteDistributeur,
            compteClient,
            request.getMontant(),
            0.0,
            request.getMontant(),
            distributeur,
            null
        );
        
        return TransactionResponse.fromTransaction(transaction);
    }
}
