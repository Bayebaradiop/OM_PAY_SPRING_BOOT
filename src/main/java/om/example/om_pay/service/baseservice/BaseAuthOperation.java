package om.example.om_pay.service.baseservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.security.JwtTokenProvider;
import om.example.om_pay.service.EmailService;
import om.example.om_pay.utils.CodeSecretUtil;

/**
 * Classe abstraite de base pour toutes les opérations d'authentification.
 * Contient les dépendances communes et les méthodes utilitaires partagées.
 * 
 * Pattern: Template Method
 * - Fournit les méthodes communes (génération token, envoi email, validation, etc.)
 * - Force les classes filles à implémenter leur logique spécifique
 */
public abstract class BaseAuthOperation {
    
    // ========== DÉPENDANCES COMMUNES ==========
    
    protected final UtilisateurRepository utilisateurRepository;
    protected final CompteRepository compteRepository;
    protected final PasswordEncoder passwordEncoder;
    protected final JwtTokenProvider tokenProvider;
    protected final EmailService emailService;
    protected final CodeSecretUtil codeSecretUtil;
    
    @Value("${code.secret.expiration}")
    protected int codeSecretExpiration;
    
    /**
     * Constructeur avec injection de toutes les dépendances communes
     */
    protected BaseAuthOperation(
            UtilisateurRepository utilisateurRepository,
            CompteRepository compteRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            EmailService emailService,
            CodeSecretUtil codeSecretUtil) {
        this.utilisateurRepository = utilisateurRepository;
        this.compteRepository = compteRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
        this.codeSecretUtil = codeSecretUtil;
    }
    
    // ========== MÉTHODES COMMUNES PROTÉGÉES ==========
    
    /**
     * Génère un token JWT pour un utilisateur
     * 
     * @param telephone - Numéro de téléphone de l'utilisateur
     * @return String - Token JWT généré
     */
    protected String genererToken(String telephone) {
        return tokenProvider.generateToken(telephone);
    }
    
    /**
     * Récupère un utilisateur par son téléphone
     * 
     * @param telephone - Numéro de téléphone
     * @return Utilisateur trouvé
     * @throws ResourceNotFoundException si l'utilisateur n'existe pas
     */
    protected Utilisateur getUtilisateurByTelephone(String telephone) {
        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }
    
    /**
     * Génère un numéro de compte unique
     * 
     * @return String - Numéro de compte au format "ACC" + timestamp
     */
    protected String genererNumeroCompte() {
        return "ACC" + System.currentTimeMillis();
    }
    
    /**
     * Envoie un code secret par email
     * 
     * @param email - Email destinataire
     * @param nom - Nom du destinataire
     * @param codeSecret - Code à envoyer
     */
    protected void envoyerCodeSecretEmail(String email, String nom, String codeSecret) {
        emailService.envoyerCodeSecret(email, nom, codeSecret);
    }
    
    /**
     * Vérifie si un mot de passe correspond au hash stocké
     * 
     * @param motDePasseClair - Mot de passe en clair
     * @param motDePasseHash - Hash stocké en base
     * @return boolean - true si correspond
     */
    protected boolean verifierMotDePasse(String motDePasseClair, String motDePasseHash) {
        return passwordEncoder.matches(motDePasseClair, motDePasseHash);
    }
    
    /**
     * Encode un mot de passe
     * 
     * @param motDePasse - Mot de passe en clair
     * @return String - Mot de passe encodé
     */
    protected String encoderMotDePasse(String motDePasse) {
        return passwordEncoder.encode(motDePasse);
    }
}
