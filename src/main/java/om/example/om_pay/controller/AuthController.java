package om.example.om_pay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import om.example.om_pay.dto.request.ChangePasswordRequest;
import om.example.om_pay.dto.request.LoginRequest;
import om.example.om_pay.dto.request.RegisterRequest;
import om.example.om_pay.dto.request.VerifyCodeSecretRequest;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.dto.response.UtilisateurResponse;
import om.example.om_pay.service.IAuthService;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.utils.CookieUtil;

/**
 * Contrôleur REST pour les endpoints d'authentification
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;
    private final UtilisateurRepository utilisateurRepository;
    private final CookieUtil cookieUtil;

    public AuthController(
        IAuthService authService,
        UtilisateurRepository utilisateurRepository,
        CookieUtil cookieUtil
    ) {
        this.authService = authService;
        this.utilisateurRepository = utilisateurRepository;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        
        AuthResponse authResponse = authService.register(request);
        // Pas de cookie JWT car le compte nécessite validation du code secret
        
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }
    
    @PostMapping("/verify-code-secret")
    public ResponseEntity<AuthResponse> verifyCodeSecret(
            @Valid @RequestBody VerifyCodeSecretRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.verifierCodeSecret(request);
        
        // Créer le cookie JWT après validation du code secret
        if (authResponse.getToken() != null) {
            cookieUtil.createJwtCookie(authResponse.getToken(), response);
        }
        
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/resend-code-secret")
    public ResponseEntity<Void> resendCodeSecret(@org.springframework.web.bind.annotation.RequestParam String telephone) {
        authService.renvoyerCodeSecret(telephone);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.login(request);
        cookieUtil.createJwtCookie(authResponse.getToken(), response);
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

   
    @GetMapping("/me")
    public ResponseEntity<UtilisateurResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();
        
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Mapper vers DTO pour éviter lazy loading exception
        UtilisateurResponse userResponse = new UtilisateurResponse();
        userResponse.setId(utilisateur.getId());
        userResponse.setNom(utilisateur.getNom());
        userResponse.setPrenom(utilisateur.getPrenom());
        userResponse.setTelephone(utilisateur.getTelephone());
        userResponse.setEmail(utilisateur.getEmail());
        userResponse.setRole(utilisateur.getRole());
        userResponse.setStatut(utilisateur.getStatut());
        userResponse.setDateCreation(utilisateur.getDateCreation());
        
        return ResponseEntity.ok(userResponse);
    }

  
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        AuthResponse authResponse = authService.refreshToken(jwt);
        return ResponseEntity.ok(authResponse);
    }

 
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieUtil.deleteJwtCookie(response);
        return ResponseEntity.ok().build();
    }
}
