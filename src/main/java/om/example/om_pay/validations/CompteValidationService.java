package om.example.om_pay.validations;

import org.springframework.stereotype.Service;

import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;

/**
 * Service de validation et récupération des comptes
 * Responsabilité : Validation des règles métier liées aux comptes
 */
@Service
public class CompteValidationService {

    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CompteValidationService(
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository) {
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère le compte principal d'un utilisateur
     */
    public Compte getComptePrincipal(Utilisateur utilisateur) {
        return utilisateur.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte principal non trouvé"));
    }

    /**
     * Récupère le compte principal d'un utilisateur par téléphone
     */
    public Compte getCompteByTelephone(String telephone) {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec le téléphone: " + telephone));
        
        return getComptePrincipal(utilisateur);
    }

    /**
     * Récupère un compte par son numéro
     */
    public Compte getCompteByNumero(String numeroCompte) {
        return compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé: " + numeroCompte));
    }

    /**
     * Vérifie que le solde est suffisant
     */
    public void verifierSolde(Compte compte, Double montantRequis) {
        if (compte.getSolde() < montantRequis) {
            throw new BadRequestException("Solde insuffisant. Solde actuel: " + compte.getSolde() + " FCFA");
        }
    }

    /**
     * Vérifie que le compte appartient à l'utilisateur
     */
    public void verifierProprietaire(Compte compte, Utilisateur utilisateur) {
        if (!compte.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new UnauthorizedException("Accès non autorisé à ce compte");
        }
    }

    /**
     * Vérifie qu'il ne s'agit pas d'un auto-transfert
     */
    public void verifierPasAutoTransfert(Compte compteExpediteur, Compte compteDestinataire) {
        if (compteExpediteur.getId().equals(compteDestinataire.getId())) {
            throw new BadRequestException("Impossible de transférer vers votre propre compte");
        }
    }
}
