package om.example.om_pay.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import om.example.om_pay.dto.request.DepotRequest;
import om.example.om_pay.dto.request.PaiementRequest;
import om.example.om_pay.dto.request.RetraitRequest;
import om.example.om_pay.dto.request.TransfertRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.service.ITransactionService;
import om.example.om_pay.service.factory.TransactionStrategyFactory;
import om.example.om_pay.service.calculation.FraisCalculService;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.TransactionRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.validations.CompteValidationService;

/**
 * Implémentation du service de gestion des transactions.
 * 
 * Architecture : Strategy Pattern
 * - Délègue les opérations transactionnelles (TRANSFERT, DEPOT, RETRAIT, PAIEMENT) 
 *   aux strategies spécialisées via TransactionStrategyFactory
 * - Gère les opérations transverses (historique, annulation, calcul frais)
 * 
 * Avantages :
 * - SRP : Chaque type de transaction dans sa propre classe
 * - OCP : Ajout nouveau type = nouvelle strategy, 0 modification ici
 * - Lisibilité : ~100 lignes au lieu de 351
 */
@Service
public class TransactionServiceImpl implements ITransactionService {

    private final TransactionStrategyFactory strategyFactory;
    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CompteValidationService compteValidationService;
    private final FraisCalculService fraisCalculService;

    public TransactionServiceImpl(
            TransactionStrategyFactory strategyFactory,
            TransactionRepository transactionRepository,
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository,
            CompteValidationService compteValidationService,
            FraisCalculService fraisCalculService) {
        this.strategyFactory = strategyFactory;
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.compteValidationService = compteValidationService;
        this.fraisCalculService = fraisCalculService;
    }

    // ========== DÉLÉGATION AUX STRATEGIES ==========

    @Override
    public TransactionResponse transfert(TransfertRequest request) {
        return strategyFactory.getStrategy(TypeTransaction.TRANSFERT).executer(request);
    }

    @Override
    public TransactionResponse depot(DepotRequest request) {
        return strategyFactory.getStrategy(TypeTransaction.DEPOT).executer(request);
    }

    @Override
    public TransactionResponse retrait(RetraitRequest request) {
        return strategyFactory.getStrategy(TypeTransaction.RETRAIT).executer(request);
    }

    @Override
    public TransactionResponse paiement(PaiementRequest request) {
        return strategyFactory.getStrategy(TypeTransaction.PAIEMENT).executer(request);
    }
    
    // ========== MÉTHODES TRANSVERSES ==========

    @Override
    public List<TransactionResponse> getHistorique(String numeroCompte) {
        Utilisateur currentUser = getCurrentUser();
        
        Compte compte = compteValidationService.getCompteByNumero(numeroCompte);
        compteValidationService.verifierProprietaire(compte, currentUser);

        List<Transaction> transactions = transactionRepository.findByCompteId(compte.getId());

        return transactions.stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public Transaction getByReference(String reference) {
        return transactionRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction non trouvée"));
    }

    @Override
    public Double calculerFrais(Double montant, String typeTransaction) {
        TypeTransaction type = TypeTransaction.valueOf(typeTransaction);
        return fraisCalculService.calculerFrais(type, montant);
    }

    @Override
    public void annuler(String reference) {
        Utilisateur currentUser = getCurrentUser();
        Transaction transaction = getByReference(reference);
        
        if (transaction.getStatut() != StatutTransaction.REUSSI) {
            throw new BadRequestException("Seules les transactions réussies peuvent être annulées");
        }

        // Vérifier que l'utilisateur est l'expéditeur de la transaction
        if (transaction.getCompteExpediteur() != null) {
            if (!transaction.getCompteExpediteur().getUtilisateur().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Vous ne pouvez annuler que vos propres transactions");
            }
        }

        // Inverser les opérations
        Compte compteExpediteur = transaction.getCompteExpediteur();
        Compte compteDestinataire = transaction.getCompteDestinataire();

        if (compteExpediteur != null) {
            compteExpediteur.setSolde(compteExpediteur.getSolde() + transaction.getMontantTotal());
        }

        if (compteDestinataire != null) {
            compteDestinataire.setSolde(compteDestinataire.getSolde() - transaction.getMontant());
        }

        transaction.setStatut(StatutTransaction.ANNULE);

        if (compteExpediteur != null) compteRepository.save(compteExpediteur);
        if (compteDestinataire != null) compteRepository.save(compteDestinataire);
        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionResponse> getHistoriqueByPeriode(String numeroCompte, LocalDateTime dateDebut, LocalDateTime dateFin) {
        Utilisateur currentUser = getCurrentUser();
        
        Compte compte = compteValidationService.getCompteByNumero(numeroCompte);
        compteValidationService.verifierProprietaire(compte, currentUser);

        List<Transaction> transactions = transactionRepository.findByCompteIdAndDateBetween(
                compte.getId(), dateDebut, dateFin);

        return transactions.stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Récupère l'utilisateur connecté
     */
    private Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();

        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));
    }
}
