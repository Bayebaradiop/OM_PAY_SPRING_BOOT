package om.example.om_pay.service.strategy.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.ChangePasswordRequest;
import om.example.om_pay.exception.BadRequestException;
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
 * Operation de changement de mot de passe.
 * 
 * Spécificités :
 * - Vérification ancien mot de passe
 * - Encodage nouveau mot de passe
 * - Mise à jour en base
 */
@Service
public class ChangePasswordOperation extends BaseAuthOperation implements IAuthOperation<ChangePasswordRequest, Void> {
    
    public ChangePasswordOperation(
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
        return TypeAuthOperation.CHANGE_PASSWORD;
    }
    
    @Override
    public String getNomOperation() {
        return "changePassword";
    }
    
    @Override
    @Transactional
    public Void executer(ChangePasswordRequest request) {
        // 1. Rechercher l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé"));

        // 2. Vérifier l'ancien mot de passe
        if (!verifierMotDePasse(request.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new UnauthorizedException("Ancien mot de passe incorrect");
        }

        // 3. Changer le mot de passe
        utilisateur.setMotDePasse(encoderMotDePasse(request.getNouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);
        
        return null;
    }
}
