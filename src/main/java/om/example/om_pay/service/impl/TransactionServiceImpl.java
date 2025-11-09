package om.example.om_pay.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.DepotRequest;
import om.example.om_pay.dto.request.PaiementRequest;
import om.example.om_pay.dto.request.RetraitRequest;
import om.example.om_pay.dto.request.TransfertRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.interfaces.ITransactionService;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Marchand;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.MarchandRepository;
import om.example.om_pay.repository.TransactionRepository;
import om.example.om_pay.repository.UtilisateurRepository;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private MarchandRepository marchandRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TransactionResponse transfert(TransfertRequest request) {
        // 1. Récupérer l'utilisateur connecté (expéditeur)
        Utilisateur expediteur = getCurrentUser();

        // 2. Récupérer le compte principal de l'expéditeur
        Compte compteExpediteur = expediteur.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte expéditeur non trouvé"));

        // 4. Récupérer le destinataire par téléphone
        Utilisateur destinataire = utilisateurRepository.findByTelephone(request.getTelephoneDestinataire())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire non trouvé"));

        // 5. Récupérer le compte principal du destinataire
        Compte compteDestinataire = destinataire.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte destinataire non trouvé"));

        // 6. Vérifier que ce n'est pas un auto-transfert
        if (compteExpediteur.getId().equals(compteDestinataire.getId())) {
            throw new BadRequestException("Impossible de transférer vers votre propre compte");
        }

        // 7. Calculer les frais (0.85% du montant)
        Double frais = request.getMontant() * 0.0085;
        Double montantTotal = request.getMontant() + frais;

        // 8. Vérifier le solde de l'expéditeur
        if (compteExpediteur.getSolde() < montantTotal) {
            throw new BadRequestException("Solde insuffisant");
        }

        // 9. Vérifier le plafond quotidien
        Double totalTransfertJour = expediteur.getTotalTransfertJour();
        if (totalTransfertJour + request.getMontant() > expediteur.getPlafondQuotidien()) {
            throw new BadRequestException("Plafond quotidien dépassé");
        }

        // 10. Effectuer le transfert
        compteExpediteur.setSolde(compteExpediteur.getSolde() - montantTotal);
        compteDestinataire.setSolde(compteDestinataire.getSolde() + request.getMontant());

        // 11. Mettre à jour le total transfert du jour
        expediteur.setTotalTransfertJour(totalTransfertJour + request.getMontant());

        // 12. Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setReference(genererReference());
        transaction.setTypeTransaction(TypeTransaction.TRANSFERT);
        transaction.setMontant(request.getMontant());
        transaction.setFrais(frais);
        transaction.setMontantTotal(montantTotal);
        transaction.setStatut(StatutTransaction.REUSSI);
        transaction.setCompteExpediteur(compteExpediteur);
        transaction.setCompteDestinataire(compteDestinataire);

        // 13. Sauvegarder
        compteRepository.save(compteExpediteur);
        compteRepository.save(compteDestinataire);
        utilisateurRepository.save(expediteur);
        transaction = transactionRepository.save(transaction);

        // 14. Retourner la réponse
        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse depot(DepotRequest request) {
        // 1. Récupérer le distributeur connecté
        Utilisateur distributeur = getCurrentUser();

        // 2. Vérifier que l'utilisateur est bien un DISTRIBUTEUR
        if (distributeur.getRole() != Role.DISTRIBUTEUR) {
            throw new UnauthorizedException("Seuls les distributeurs peuvent effectuer des dépôts");
        }

        // 3. Récupérer le compte du distributeur
        Compte compteDistributeur = distributeur.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte distributeur non trouvé"));

        // 4. Récupérer le client par téléphone
        Utilisateur client = utilisateurRepository.findByTelephone(request.getTelephoneClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        // 5. Récupérer le compte du client
        Compte compteClient = client.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte client non trouvé"));

        // 6. Vérifier le solde du distributeur
        if (compteDistributeur.getSolde() < request.getMontant()) {
            throw new BadRequestException("Solde distributeur insuffisant");
        }

        // 7. Effectuer le dépôt (pas de frais pour dépôt)
        compteDistributeur.setSolde(compteDistributeur.getSolde() - request.getMontant());
        compteClient.setSolde(compteClient.getSolde() + request.getMontant());

        // 8. Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setReference(genererReference());
        transaction.setTypeTransaction(TypeTransaction.DEPOT);
        transaction.setMontant(request.getMontant());
        transaction.setFrais(0.0);
        transaction.setMontantTotal(request.getMontant());
        transaction.setStatut(StatutTransaction.REUSSI);
        transaction.setCompteExpediteur(compteDistributeur);
        transaction.setCompteDestinataire(compteClient);
        transaction.setDistributeur(distributeur);

        // 9. Sauvegarder
        compteRepository.save(compteDistributeur);
        compteRepository.save(compteClient);
        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse retrait(RetraitRequest request) {
        // 1. Récupérer le distributeur connecté
        Utilisateur distributeur = getCurrentUser();

        // 2. Vérifier que l'utilisateur est bien un DISTRIBUTEUR
        if (distributeur.getRole() != Role.DISTRIBUTEUR) {
            throw new UnauthorizedException("Seuls les distributeurs peuvent effectuer des retraits");
        }

        // 3. Récupérer le client par téléphone
        Utilisateur client = utilisateurRepository.findByTelephone(request.getTelephoneClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        // 4. Récupérer le compte du client
        Compte compteClient = client.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte client non trouvé"));

        // 6. Récupérer le compte du distributeur
        Compte compteDistributeur = distributeur.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte distributeur non trouvé"));

        // 7. Aucun frais pour les retraits
        Double frais = 0.0;
        Double montantTotal = request.getMontant();

        // 8. Vérifier le solde du client
        if (compteClient.getSolde() < montantTotal) {
            throw new BadRequestException("Solde client insuffisant");
        }

        // 9. Effectuer le retrait
        compteClient.setSolde(compteClient.getSolde() - montantTotal);
        compteDistributeur.setSolde(compteDistributeur.getSolde() + request.getMontant());

        // 10. Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setReference(genererReference());
        transaction.setTypeTransaction(TypeTransaction.RETRAIT);
        transaction.setMontant(request.getMontant());
        transaction.setFrais(frais);
        transaction.setMontantTotal(montantTotal);
        transaction.setStatut(StatutTransaction.REUSSI);
        transaction.setCompteExpediteur(compteClient);
        transaction.setCompteDestinataire(compteDistributeur);
        transaction.setDistributeur(distributeur);

        // 11. Sauvegarder
        compteRepository.save(compteClient);
        compteRepository.save(compteDistributeur);
        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse paiement(PaiementRequest request) {
        // 1. Récupérer l'utilisateur connecté (client)
        Utilisateur client = getCurrentUser();

        // 2. Récupérer le compte du client
        Compte compteClient = client.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte client non trouvé"));

        // 4. Récupérer le marchand par code
        Marchand marchand = marchandRepository.findByCodeMarchand(request.getCodeMarchand())
                .orElseThrow(() -> new ResourceNotFoundException("Marchand non trouvé"));

        // 5. Calculer les frais (0.85% du montant)
        Double frais = request.getMontant() * 0.0085;
        Double montantTotal = request.getMontant() + frais;

        // 6. Vérifier le solde du client
        if (compteClient.getSolde() < montantTotal) {
            throw new BadRequestException("Solde insuffisant");
        }

        // 7. Effectuer le paiement (débiter le client)
        compteClient.setSolde(compteClient.getSolde() - montantTotal);

        // 8. Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setReference(genererReference());
        transaction.setTypeTransaction(TypeTransaction.PAIEMENT);
        transaction.setMontant(request.getMontant());
        transaction.setFrais(frais);
        transaction.setMontantTotal(montantTotal);
        transaction.setStatut(StatutTransaction.REUSSI);
        transaction.setCompteExpediteur(compteClient);
        transaction.setMarchand(marchand);

        // 9. Sauvegarder
        compteRepository.save(compteClient);
        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getHistorique(String numeroCompte) {
        Utilisateur currentUser = getCurrentUser();
        
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        // Vérifier que le compte appartient à l'utilisateur connecté
        if (!compte.getUtilisateur().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Accès non autorisé à l'historique de ce compte");
        }

        List<Transaction> transactions = transactionRepository.findByCompteId(compte.getId());

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Transaction getByReference(String reference) {
        return transactionRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction non trouvée"));
    }

    @Override
    public Double calculerFrais(Double montant, String typeTransaction) {
        switch (typeTransaction) {
            case "TRANSFERT":
                return montant * 0.0085; // 0.85%
            case "RETRAIT":
                return 0.0; // Aucun frais
            case "PAIEMENT":
                return montant * 0.0085; // 0.85%
            case "DEPOT":
                return 0.0; // Aucun frais
            default:
                return 0.0;
        }
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
        
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        // Vérifier que le compte appartient à l'utilisateur connecté
        if (!compte.getUtilisateur().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Accès non autorisé à l'historique de ce compte");
        }

        List<Transaction> transactions = transactionRepository.findByCompteIdAndDateBetween(
                compte.getId(), dateDebut, dateFin);

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Récupère l'utilisateur connecté
     */
    private Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();

        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));
    }

    /**
     * Génère une référence unique pour la transaction
     */
    private String genererReference() {
        String reference;
        do {
            reference = "TRX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (transactionRepository.existsByReference(reference));

        return reference;
    }

    /**
     * Convertit une Transaction en TransactionResponse
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setReference(transaction.getReference());
        response.setTypeTransaction(transaction.getTypeTransaction());
        response.setMontant(transaction.getMontant());
        response.setFrais(transaction.getFrais());
        response.setMontantTotal(transaction.getMontantTotal());
        response.setStatut(transaction.getStatut());
        response.setDateCreation(transaction.getDateTransaction());

        if (transaction.getCompteExpediteur() != null) {
            response.setCompteExpediteur(transaction.getCompteExpediteur().getNumeroCompte());
            // Ajouter le nouveau solde de l'expéditeur après la transaction
            response.setNouveauSolde(transaction.getCompteExpediteur().getSolde());
        }

        if (transaction.getCompteDestinataire() != null) {
            response.setCompteDestinataire(transaction.getCompteDestinataire().getNumeroCompte());
        }

        if (transaction.getDistributeur() != null) {
            response.setTelephoneDistributeur(transaction.getDistributeur().getTelephone());
        }

        if (transaction.getMarchand() != null) {
            response.setNomMarchand(transaction.getMarchand().getNomCommercial());
        }

        return response;
    }
}
