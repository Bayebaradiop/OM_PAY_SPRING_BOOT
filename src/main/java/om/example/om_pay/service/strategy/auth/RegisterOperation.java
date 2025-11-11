package om.example.om_pay.service.strategy.auth;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.RegisterRequest;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.event.CodeSecretEnvoyeEvent;
import om.example.om_pay.event.CompteCreationEvent;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeCompte;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.model.enums.TypeAuthOperation;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.security.JwtTokenProvider;
import om.example.om_pay.service.EmailService;
import om.example.om_pay.service.baseservice.BaseAuthOperation;
import om.example.om_pay.utils.CodeSecretUtil;

/**
 * Operation d'inscription d'un nouvel utilisateur.
 * 
 * Spécificités :
 * - Vérification unicité téléphone et email
 * - Création utilisateur + compte principal
 * - Génération et envoi code secret par email
 * - Pas de token JWT généré (nécessite validation code secret)
 */
@Service
public class RegisterOperation extends BaseAuthOperation implements IAuthOperation<RegisterRequest, AuthResponse> {
    
    private static final Logger log = LoggerFactory.getLogger(RegisterOperation.class);
    private final ApplicationEventPublisher eventPublisher;
    
    public RegisterOperation(
            UtilisateurRepository utilisateurRepository,
            CompteRepository compteRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            EmailService emailService,
            CodeSecretUtil codeSecretUtil,
            ApplicationEventPublisher eventPublisher) {
        super(utilisateurRepository, compteRepository, passwordEncoder, 
              tokenProvider, emailService, codeSecretUtil);
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public TypeAuthOperation getType() {
        return TypeAuthOperation.REGISTER;
    }
    
    @Override
    public String getNomOperation() {
        return "register";
    }
    
    @Override
    @Transactional
    public AuthResponse executer(RegisterRequest request) {
        // 1. Vérifier si le téléphone existe déjà
        if (utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new BadRequestException("Ce numéro de téléphone est déjà utilisé");
        }

        // 2. Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        // 3. Créer l'utilisateur
        Utilisateur utilisateur = creerUtilisateur(request);
        
        // 4. Générer et définir le code secret
        String codeSecret = codeSecretUtil.genererCodeSecret();
        utilisateur.setCodeSecret(codeSecret);
        utilisateur.setCodeSecretExpiration(
            codeSecretUtil.calculerExpiration(codeSecretExpiration)
        );
        utilisateur.setPremiereConnexion(true);

        // 5. Sauvegarder l'utilisateur
        utilisateur = utilisateurRepository.save(utilisateur);

        // 6. Créer automatiquement un compte principal
        Compte comptePrincipal = creerComptePrincipal(utilisateur);
        
        // 7. Publier événement pour envoi code secret (asynchrone)
        eventPublisher.publishEvent(new CodeSecretEnvoyeEvent(
            this,
            utilisateur.getEmail(),
            codeSecret,
            utilisateur.getPrenom() + " " + utilisateur.getNom()
        ));
        
        // 8. Publier événement pour confirmation création compte (asynchrone)
        eventPublisher.publishEvent(new CompteCreationEvent(
            this,
            comptePrincipal,
            utilisateur.getEmail()
        ));
        
        log.info("Événements de création publiés pour l'utilisateur {}", utilisateur.getTelephone());

        // 8. Retourner la réponse sans token (validation code secret requise)
        return new AuthResponse(
            null, // Pas de token avant vérification du code
            utilisateur.getTelephone(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getRole()
        );
    }
    
    /**
     * Crée un nouvel utilisateur à partir de la requête
     */
    private Utilisateur creerUtilisateur(RegisterRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(encoderMotDePasse(request.getMotDePasse()));
        
        // Code PIN optionnel - peut être null
        if (request.getCodePin() != null && !request.getCodePin().trim().isEmpty()) {
            utilisateur.setCodePin(encoderMotDePasse(request.getCodePin()));
        }
        
        utilisateur.setRole(request.getRole());
        utilisateur.setStatut(Statut.ACTIF);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateur.setPlafondQuotidien(1000000.0); // 1 000 000 FCFA par défaut
        utilisateur.setTotalTransfertJour(0.0);
        return utilisateur;
    }
    
    /**
     * Crée un compte principal pour l'utilisateur
     */
    private Compte creerComptePrincipal(Utilisateur utilisateur) {
        Compte compte = new Compte();
        compte.setNumeroCompte(genererNumeroCompte());
        compte.setSolde(0.0);
        compte.setTypeCompte(TypeCompte.PRINCIPAL);
        compte.setStatut(Statut.ACTIF);
        compte.setDateCreation(LocalDateTime.now());
        compte.setUtilisateur(utilisateur);
        
        return compteRepository.save(compte);
    }
}
