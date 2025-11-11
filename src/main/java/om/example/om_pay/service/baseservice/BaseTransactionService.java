package om.example.om_pay.service.baseservice;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Marchand;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.TransactionRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.calculation.FraisCalculService;
import om.example.om_pay.service.calculation.PlafondService;
import om.example.om_pay.service.strategy.transaction.ITransactionStrategy;
import om.example.om_pay.utils.ReferenceGeneratorService;
import om.example.om_pay.validations.CompteValidationService;

/**
 * Classe abstraite de base pour toutes les strategies de transaction.
 * Contient les dépendances communes et les méthodes utilitaires partagées.
 * 
 * Pattern: Template Method + Strategy
 * - Fournit les méthodes communes (getUtilisateurConnecte, creerTransaction, etc.)
 * - Force les classes filles à implémenter executer() et getType()
 */
public abstract class BaseTransactionService implements ITransactionStrategy {
    
    // ========== DÉPENDANCES COMMUNES ==========
    
    protected final TransactionRepository transactionRepository;
    protected final CompteRepository compteRepository;
    protected final UtilisateurRepository utilisateurRepository;
    protected final CompteValidationService compteValidationService;
    protected final FraisCalculService fraisCalculService;
    protected final PlafondService plafondService;
    protected final ReferenceGeneratorService referenceGeneratorService;
    
    /**
     * Constructeur avec injection de toutes les dépendances communes
     */
    protected BaseTransactionService(
            TransactionRepository transactionRepository,
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository,
            CompteValidationService compteValidationService,
            FraisCalculService fraisCalculService,
            PlafondService plafondService,
            ReferenceGeneratorService referenceGeneratorService) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.compteValidationService = compteValidationService;
        this.fraisCalculService = fraisCalculService;
        this.plafondService = plafondService;
        this.referenceGeneratorService = referenceGeneratorService;
    }
    
    // ========== MÉTHODES COMMUNES PROTÉGÉES ==========
    
    /**
     * Récupère l'utilisateur actuellement connecté via Spring Security
     * 
     * @return Utilisateur connecté
     * @throws UnauthorizedException si aucun utilisateur authentifié
     */
    protected Utilisateur getUtilisateurConnecte() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();
        
        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));
    }
    
    /**
     * Crée et enregistre une transaction en base de données
     * 
     * @param type - Type de transaction (TRANSFERT, DEPOT, RETRAIT, PAIEMENT)
     * @param compteExpediteur - Compte source (peut être null pour certains types)
     * @param compteDestinataire - Compte destination (peut être null pour paiement)
     * @param montant - Montant de la transaction
     * @param frais - Frais appliqués
     * @param montantTotal - Montant total (montant + frais)
     * @param distributeur - Distributeur concerné (null si pas de distributeur)
     * @param marchand - Marchand concerné (null si pas de marchand)
     * @return Transaction sauvegardée
     */
    protected Transaction creerTransaction(
            TypeTransaction type,
            Compte compteExpediteur,
            Compte compteDestinataire,
            Double montant,
            Double frais,
            Double montantTotal,
            Utilisateur distributeur,
            Marchand marchand) {
        
        Transaction transaction = new Transaction();
        transaction.setReference(referenceGeneratorService.genererReference());
        transaction.setTypeTransaction(type);
        transaction.setMontant(montant);
        transaction.setFrais(frais);
        transaction.setMontantTotal(montantTotal);
        transaction.setStatut(StatutTransaction.REUSSI);
        transaction.setCompteExpediteur(compteExpediteur);
        transaction.setCompteDestinataire(compteDestinataire);
        transaction.setDistributeur(distributeur);
        transaction.setMarchand(marchand);
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Effectue un transfert d'argent entre deux comptes
     * Débite le compte source et crédite le compte destination
     * 
     * @param source - Compte à débiter
     * @param destination - Compte à créditer
     * @param montantDebite - Montant à débiter (peut inclure les frais)
     * @param montantCredite - Montant à créditer
     */
    protected void effectuerTransfertComptes(
            Compte source, 
            Compte destination, 
            Double montantDebite, 
            Double montantCredite) {
        
        source.setSolde(source.getSolde() - montantDebite);
        destination.setSolde(destination.getSolde() + montantCredite);
        compteRepository.save(source);
        compteRepository.save(destination);
    }
    
    /**
     * Vérifie que l'utilisateur a le rôle DISTRIBUTEUR
     * 
     * @param utilisateur - Utilisateur à vérifier
     * @throws UnauthorizedException si l'utilisateur n'est pas distributeur
     */
    protected void verifierRoleDistributeur(Utilisateur utilisateur) {
        if (utilisateur.getRole() != Role.DISTRIBUTEUR) {
            throw new UnauthorizedException(
                "Seuls les distributeurs peuvent effectuer cette opération"
            );
        }
    }
}
