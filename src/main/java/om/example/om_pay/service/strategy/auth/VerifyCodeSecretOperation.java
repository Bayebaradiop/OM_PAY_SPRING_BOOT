package om.example.om_pay.service.strategy.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.VerifyCodeSecretRequest;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.TypeAuthOperation;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.security.JwtTokenProvider;
import om.example.om_pay.service.EmailService;
import om.example.om_pay.service.baseservice.BaseAuthOperation;
import om.example.om_pay.utils.CodeSecretUtil;

/**
 * Operation de vérification du code secret lors de la première connexion.
 * 
 * Spécificités :
 * - Vérification code secret
 * - Vérification expiration (30 minutes)
 * - Activation compte (premiereConnexion = false)
 * - Suppression code après validation
 * - Génération token JWT
 */
@Service
public class VerifyCodeSecretOperation extends BaseAuthOperation implements IAuthOperation<VerifyCodeSecretRequest, AuthResponse> {
    
    private static final Logger log = LoggerFactory.getLogger(VerifyCodeSecretOperation.class);
    
    public VerifyCodeSecretOperation(
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
        return TypeAuthOperation.VERIFY_CODE_SECRET;
    }
    
    @Override
    public String getNomOperation() {
        return "verifyCodeSecret";
    }
    
    @Override
    @Transactional
    public AuthResponse executer(VerifyCodeSecretRequest request) {
        // 1. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(request.getTelephone())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        // 2. Vérifier si c'est la première connexion
        if (Boolean.FALSE.equals(utilisateur.getPremiereConnexion())) {
            throw new BadRequestException(
                "Le code secret a déjà été utilisé. Utilisez votre mot de passe pour vous connecter."
            );
        }
        
        // 3. Vérifier le code secret
        if (!request.getCodeSecret().equals(utilisateur.getCodeSecret())) {
            throw new BadRequestException("Code secret incorrect");
        }
        
        // 4. Vérifier l'expiration
        if (codeSecretUtil.estExpire(utilisateur.getCodeSecretExpiration())) {
            throw new BadRequestException(
                "Le code secret a expiré. Veuillez demander un nouveau code."
            );
        }
        
        // 5. Marquer comme validé et supprimer le code
        utilisateur.setPremiereConnexion(false);
        utilisateur.setCodeSecret(null);
        utilisateur.setCodeSecretExpiration(null);
        utilisateurRepository.save(utilisateur);
        
        // 6. Générer le token JWT
        String token = genererToken(utilisateur.getTelephone());
        
        log.info("Code secret validé pour l'utilisateur {}", utilisateur.getTelephone());
        
        return new AuthResponse(
            token,
            utilisateur.getTelephone(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getRole()
        );
    }
}
