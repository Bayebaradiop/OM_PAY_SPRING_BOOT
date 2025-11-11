package om.example.om_pay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import om.example.om_pay.config.ApiResponse;
import om.example.om_pay.dto.request.ChangePasswordRequest;
import om.example.om_pay.dto.request.LoginRequest;
import om.example.om_pay.dto.request.RegisterRequest;
import om.example.om_pay.dto.request.VerifyCodeSecretRequest;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.dto.response.ProfilCompletResponse;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.IAuthService;
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
    @Operation(
        summary = "Récupérer le profil complet de l'utilisateur connecté",
        description = "Retourne toutes les informations de l'utilisateur : compte, solde, statistiques et dernières transactions",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<ProfilCompletResponse>> getProfilComplet() {
        ProfilCompletResponse profil = authService.getProfilComplet();
        
        ApiResponse<ProfilCompletResponse> response = ApiResponse.success(
            "Profil récupéré avec succès",
            profil
        );
        
        return ResponseEntity.ok(response);
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
