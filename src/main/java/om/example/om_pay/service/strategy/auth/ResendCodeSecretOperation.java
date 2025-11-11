package om.example.om_pay.service.strategy.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * Operation de renvoi du code secret.
 * 
 * Spécificités :
 * - Vérification compte non activé (premiereConnexion = true)
 * - Génération nouveau code secret
 * - Nouvelle expiration (30 minutes)
 * - Envoi email
 */
@Service
public class ResendCodeSecretOperation extends BaseAuthOperation implements IAuthOperation<String, Void> {
    
    private static final Logger log = LoggerFactory.getLogger(ResendCodeSecretOperation.class);
    
    public ResendCodeSecretOperation(
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
        return TypeAuthOperation.RESEND_CODE_SECRET;
    }
    
    @Override
    public String getNomOperation() {
        return "resendCodeSecret";
    }
    
    @Override
    @Transactional
    public Void executer(String telephone) {
        // 1. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        // 2. Vérifier si c'est bien la première connexion
        if (Boolean.FALSE.equals(utilisateur.getPremiereConnexion())) {
            throw new BadRequestException(
                "Votre compte est déjà activé. Utilisez votre mot de passe pour vous connecter."
            );
        }
        
        // 3. Générer un nouveau code secret
        String nouveauCodeSecret = codeSecretUtil.genererCodeSecret();
        utilisateur.setCodeSecret(nouveauCodeSecret);
        utilisateur.setCodeSecretExpiration(
            codeSecretUtil.calculerExpiration(codeSecretExpiration)
        );
        utilisateurRepository.save(utilisateur);
        
        // 4. Envoyer le nouveau code par email
        try {
            String nomComplet = utilisateur.getPrenom() + " " + utilisateur.getNom();
            envoyerCodeSecretEmail(utilisateur.getEmail(), nomComplet, nouveauCodeSecret);
            log.info("Nouveau code secret envoyé à {}", utilisateur.getEmail());
        } catch (Exception e) {
            log.error("Erreur envoi email à {}: {}", utilisateur.getEmail(), e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email. Veuillez réessayer.");
        }
        
        return null;
    }
}
