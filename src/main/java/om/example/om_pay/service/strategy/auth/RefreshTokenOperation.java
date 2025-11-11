package om.example.om_pay.service.strategy.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.TypeAuthOperation;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.security.JwtTokenProvider;
import om.example.om_pay.service.EmailService;
import om.example.om_pay.service.baseservice.BaseAuthOperation;
import om.example.om_pay.utils.CodeSecretUtil;

/**
 * Operation de rafraîchissement du token JWT.
 * 
 * Spécificités :
 * - Validation du token actuel
 * - Extraction téléphone du token
 * - Génération nouveau token JWT
 */
@Service
public class RefreshTokenOperation extends BaseAuthOperation implements IAuthOperation<String, AuthResponse> {
    
    public RefreshTokenOperation(
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
        return TypeAuthOperation.REFRESH_TOKEN;
    }
    
    @Override
    public String getNomOperation() {
        return "refreshToken";
    }
    
    @Override
    public AuthResponse executer(String oldToken) {
        // 1. Valider le token
        if (!tokenProvider.validateToken(oldToken)) {
            throw new UnauthorizedException("Token invalide");
        }

        // 2. Extraire le téléphone et générer un nouveau token
        String telephone = tokenProvider.getPhoneFromToken(oldToken);
        String newToken = genererToken(telephone);

        // 3. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

        return new AuthResponse(
            newToken, 
            utilisateur.getTelephone(), 
            utilisateur.getNom(), 
            utilisateur.getPrenom(), 
            utilisateur.getRole()
        );
    }
}
