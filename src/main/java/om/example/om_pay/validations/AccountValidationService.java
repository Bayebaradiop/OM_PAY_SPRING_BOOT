package om.example.om_pay.validations;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;

/**
 * Service de validation et vérification des comptes.
 * Responsabilité : Validation des permissions et accès aux comptes
 */
@Service
public class AccountValidationService {

    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AccountValidationService(
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository) {
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère un compte et vérifie que l'utilisateur connecté est le propriétaire
     * 
     * @param numeroCompte - Numéro du compte à récupérer
     * @return Compte si l'utilisateur est autorisé
     * @throws ResourceNotFoundException si le compte n'existe pas
     * @throws UnauthorizedException si l'utilisateur n'est pas le propriétaire
     */
    public Compte getCompteWithPermission(String numeroCompte) {
        Utilisateur currentUser = getCurrentUser();
        
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        // Vérifier que le compte appartient bien à l'utilisateur
        if (!compte.getUtilisateur().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Accès non autorisé à ce compte");
        }

        return compte;
    }

    /**
     * Récupère l'utilisateur actuellement connecté
     * 
     * @return Utilisateur connecté
     * @throws UnauthorizedException si aucun utilisateur n'est connecté
     */
    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();

        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));
    }

    /**
     * Vérifie si l'utilisateur connecté est le propriétaire du compte
     * 
     * @param compte - Compte à vérifier
     * @return true si l'utilisateur est propriétaire, false sinon
     */
    public boolean isOwner(Compte compte) {
        try {
            Utilisateur currentUser = getCurrentUser();
            return compte.getUtilisateur().getId().equals(currentUser.getId());
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    /**
     * Vérifie qu'un compte existe
     * 
     * @param numeroCompte - Numéro du compte
     * @return Compte trouvé
     * @throws ResourceNotFoundException si le compte n'existe pas
     */
    public Compte verifierExistenceCompte(String numeroCompte) {
        return compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
    }
}
