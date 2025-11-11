package om.example.om_pay.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.response.ProfilCompletResponse;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Transaction;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeCompte;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.TransactionRepository;
import om.example.om_pay.repository.UtilisateurRepository;

/**
 * Service pour gérer le profil complet de l'utilisateur
 */
@Service
public class ProfilService {

    private final UtilisateurRepository utilisateurRepository;
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    
    public ProfilService(UtilisateurRepository utilisateurRepository,
                        CompteRepository compteRepository,
                        TransactionRepository transactionRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.compteRepository = compteRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Récupère le profil complet de l'utilisateur connecté
     */
    @Transactional(readOnly = true)
    public ProfilCompletResponse getProfilComplet() {
        // 1. Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();
        
        // 2. Récupérer le compte principal
        Compte comptePrincipal = compteRepository
            .findByUtilisateurAndTypeCompte(utilisateur, TypeCompte.PRINCIPAL)
            .orElseThrow(() -> new ResourceNotFoundException("Compte principal non trouvé"));
        
        // 3. Récupérer toutes les transactions
        List<Transaction> transactions = transactionRepository
            .findByCompteExpediteurOrCompteDestinataire(comptePrincipal, comptePrincipal);
        
        // 4. Calculer les statistiques
        long nombreTotal = transactions.size();
        long nombreReussies = transactions.stream()
            .filter(t -> StatutTransaction.REUSSI.equals(t.getStatut()))
            .count();
        long nombreEchouees = nombreTotal - nombreReussies;
        
        double totalEnvoye = transactions.stream()
            .filter(t -> comptePrincipal.equals(t.getCompteExpediteur()))
            .filter(t -> StatutTransaction.REUSSI.equals(t.getStatut()))
            .mapToDouble(t -> t.getMontantTotal())
            .sum();
        
        double totalRecu = transactions.stream()
            .filter(t -> t.getCompteDestinataire() != null 
                      && comptePrincipal.equals(t.getCompteDestinataire()))
            .filter(t -> StatutTransaction.REUSSI.equals(t.getStatut()))
            .mapToDouble(t -> t.getMontant())
            .sum();
        
        // 5. Récupérer les 5 dernières transactions
        List<Transaction> dernieresTransactions = transactionRepository
            .findTop5ByCompteExpediteurOrCompteDestinataireOrderByDateCreationDesc(
                comptePrincipal, comptePrincipal
            );
        
        // Limiter à 5 si plus de 5
        if (dernieresTransactions.size() > 5) {
            dernieresTransactions = dernieresTransactions.subList(0, 5);
        }
        
        // 6. Construire la réponse
        return ProfilCompletResponse.builder()
            // Informations utilisateur
            .id(utilisateur.getId())
            .nom(utilisateur.getNom())
            .prenom(utilisateur.getPrenom())
            .telephone(utilisateur.getTelephone())
            .email(utilisateur.getEmail())
            .role(utilisateur.getRole())
            .statut(utilisateur.getStatut())
            .premiereConnexion(utilisateur.getPremiereConnexion())
            .dateCreation(utilisateur.getDateCreation())
            
            // Informations du compte
            .compte(ProfilCompletResponse.CompteInfo.builder()
                .id(comptePrincipal.getId())
                .numeroCompte(comptePrincipal.getNumeroCompte())
                .solde(comptePrincipal.getSolde())
                .typeCompte(comptePrincipal.getTypeCompte())
                .statut(comptePrincipal.getStatut())
                .dateCreation(comptePrincipal.getDateCreation())
                .build())
            
            // Statistiques
            .statistiques(ProfilCompletResponse.StatistiquesUtilisateur.builder()
                .nombreTransactions(nombreTotal)
                .nombreTransactionsReussies(nombreReussies)
                .nombreTransactionsEchouees(nombreEchouees)
                .totalEnvoye(totalEnvoye)
                .totalRecu(totalRecu)
                .plafondQuotidien(utilisateur.getPlafondQuotidien())
                .totalTransfertJour(utilisateur.getTotalTransfertJour())
                .plafondRestant(utilisateur.getPlafondQuotidien() - utilisateur.getTotalTransfertJour())
                .build())
            
            // Dernières transactions
            .dernieresTransactions(dernieresTransactions.stream()
                .map(t -> ProfilCompletResponse.TransactionResume.builder()
                    .id(t.getId())
                    .reference(t.getReference())
                    .typeTransaction(t.getTypeTransaction().name())
                    .montant(t.getMontant())
                    .frais(t.getFrais())
                    .statut(t.getStatut().name())
                    .compteExpediteur(t.getCompteExpediteur().getNumeroCompte())
                    .compteDestinataire(t.getCompteDestinataire() != null 
                        ? t.getCompteDestinataire().getNumeroCompte() 
                        : null)
                    .dateCreation(t.getDateTransaction())
                    .build())
                .toList())
            .build();
    }
    
    /**
     * Récupère l'utilisateur actuellement authentifié
     */
    private Utilisateur getUtilisateurConnecte() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }
        
        String telephone = authentication.getName();
        return utilisateurRepository.findByTelephone(telephone)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }
}
