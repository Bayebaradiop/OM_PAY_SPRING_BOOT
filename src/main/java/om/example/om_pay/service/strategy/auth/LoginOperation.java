package om.example.om_pay.service.strategy.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import om.example.om_pay.dto.request.LoginRequest;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeAuthOperation;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.security.JwtTokenProvider;
import om.example.om_pay.service.EmailService;
import om.example.om_pay.service.baseservice.BaseAuthOperation;
import om.example.om_pay.utils.CodeSecretUtil;

/**
 * Operation de connexion d'un utilisateur.
 * 
 * Spécificités :
 * - Vérification identifiants (téléphone + mot de passe)
 * - Blocage si compte non activé (premiereConnexion = true)
 * - Vérification statut compte (bloqué/actif)
 * - Génération token JWT
 */
@Service
public class LoginOperation extends BaseAuthOperation implements IAuthOperation<LoginRequest, AuthResponse> {
    
    public LoginOperation(
            UtilisateurRepository utilisateurRepository,
            CompteRepository compteRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            EmailService emailService,
            CodeSecretUtil codeSecretUtil) {
        super(utilisateurRepository, compteRepository, passwordEncoder, 
              tokenProvider, emailService, codeSecretUtil);
    }
    
    @Override
    public TypeAuthOperation getType() {
        return TypeAuthOperation.LOGIN;
    }
    
    @Override
    public String getNomOperation() {
        return "login";
    }
    
    @Override
    public AuthResponse executer(LoginRequest request) {
        // 1. Rechercher l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new UnauthorizedException("Téléphone ou mot de passe incorrect"));

        // 2. Vérifier le mot de passe
        if (!verifierMotDePasse(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new UnauthorizedException("Téléphone ou mot de passe incorrect");
        }

        // 3. Vérifier si c'est la première connexion (compte non activé)
        if (Boolean.TRUE.equals(utilisateur.getPremiereConnexion())) {
            throw new UnauthorizedException(
                "Compte non activé. Veuillez valider le code secret envoyé par email avant de vous connecter."
            );
        }

        // 4. Vérifier le statut du compte
        if (utilisateur.getStatut() == Statut.BLOQUE) {
            throw new UnauthorizedException("Votre compte est bloqué. Contactez le support.");
        }

        // 5. Générer le token JWT
        String token = genererToken(utilisateur.getTelephone());

        return new AuthResponse(
            token, 
            utilisateur.getTelephone(), 
            utilisateur.getNom(), 
            utilisateur.getPrenom(), 
            utilisateur.getRole()
        );
    }
}
