package om.example.om_pay.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.ChangePasswordRequest;
import om.example.om_pay.dto.request.LoginRequest;
import om.example.om_pay.dto.request.RegisterRequest;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.interfaces.IAuthService;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeCompte;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.security.JwtTokenProvider;

/**
 * Implémentation du service d'authentification
 */
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si le téléphone existe déjà
        if (utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new BadRequestException("Ce numéro de téléphone est déjà utilisé");
        }

        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setCodePin(passwordEncoder.encode(request.getCodePin()));
        utilisateur.setRole(request.getRole());
        utilisateur.setStatut(Statut.ACTIF);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateur.setPlafondQuotidien(1000000.0); // 1 000 000 FCFA par défaut
        utilisateur.setTotalTransfertJour(0.0);

        // Sauvegarder l'utilisateur
        utilisateur = utilisateurRepository.save(utilisateur);

        // Créer automatiquement un compte principal
        Compte compte = new Compte();
        compte.setNumeroCompte(genererNumeroCompte());
        compte.setSolde(0.0);
        compte.setTypeCompte(TypeCompte.PRINCIPAL);
        compte.setStatut(Statut.ACTIF);
        compte.setDateCreation(LocalDateTime.now());
        compte.setUtilisateur(utilisateur);
        
        compteRepository.save(compte);

        // Générer le token JWT
        String token = tokenProvider.generateToken(utilisateur.getTelephone());

        return new AuthResponse(token, utilisateur.getTelephone(), 
                               utilisateur.getNom(), utilisateur.getPrenom(), 
                               utilisateur.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Rechercher l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new UnauthorizedException("Téléphone ou mot de passe incorrect"));

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new UnauthorizedException("Téléphone ou mot de passe incorrect");
        }

        // Vérifier le statut du compte
        if (utilisateur.getStatut() == Statut.BLOQUE) {
            throw new UnauthorizedException("Votre compte est bloqué. Contactez le support.");
        }

        // Générer le token JWT
        String token = tokenProvider.generateToken(utilisateur.getTelephone());

        return new AuthResponse(token, utilisateur.getTelephone(), 
                               utilisateur.getNom(), utilisateur.getPrenom(), 
                               utilisateur.getRole());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // Rechercher l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new UnauthorizedException("Ancien mot de passe incorrect");
        }

        // Changer le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public void logout(String token) {
        
    }

    @Override
    public AuthResponse refreshToken(String oldToken) {
        // Valider le token
        if (!tokenProvider.validateToken(oldToken)) {
            throw new UnauthorizedException("Token invalide");
        }

        // Extraire le téléphone et générer un nouveau token
        String telephone = tokenProvider.getPhoneFromToken(oldToken);
        String newToken = tokenProvider.generateToken(telephone);

        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

        return new AuthResponse(newToken, utilisateur.getTelephone(), 
                               utilisateur.getNom(), utilisateur.getPrenom(), 
                               utilisateur.getRole());
    }

    @Override
    public boolean telephoneExists(String telephone) {
        return utilisateurRepository.existsByTelephone(telephone);
    }

    @Override
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

   
    private String genererNumeroCompte() {
        String numeroCompte;
        do {
            long numero = (long) (Math.random() * 10_000_000_000L);
            numeroCompte = String.format("OM%010d", numero);
        } while (compteRepository.existsByNumeroCompte(numeroCompte));
        
        return numeroCompte;
    }
}
