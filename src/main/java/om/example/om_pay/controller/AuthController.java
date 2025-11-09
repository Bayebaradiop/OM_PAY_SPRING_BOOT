package om.example.om_pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import om.example.om_pay.dto.response.ApiResponse;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.dto.response.UtilisateurResponse;
import om.example.om_pay.interfaces.IAuthService;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.utils.CookieUtil;

/**
 * Contrôleur REST pour les endpoints d'authentification
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CookieUtil cookieUtil;

 
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.register(request);
        
        cookieUtil.createJwtCookie(authResponse.getToken(), response);
       
        ApiResponse<AuthResponse> apiResponse = ApiResponse.success(
            "Inscription réussie. Token disponible dans cookie et réponse.",
            authResponse
        );
        
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Connexion d'un utilisateur
     * POST /api/auth/login
     * Token stocké dans cookie HTTP-only ET retourné dans la réponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.login(request);
        
        // Stocker le token dans un cookie HTTP-only pour les clients web
        cookieUtil.createJwtCookie(authResponse.getToken(), response);
        
        // Garder le token dans la réponse pour Swagger/Postman
        // Les clients peuvent choisir d'utiliser le cookie ou le header Authorization
        
        ApiResponse<AuthResponse> apiResponse = ApiResponse.success(
            "Connexion réussie. Token disponible dans cookie et réponse.",
            authResponse
        );
        
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Changement de mot de passe
     * POST /api/auth/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        
        ApiResponse<String> response = ApiResponse.success(
            "Mot de passe changé avec succès",
            null
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les informations de l'utilisateur connecté
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getCurrentUser() {
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
        
        ApiResponse<UtilisateurResponse> response = ApiResponse.success(userResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Rafraîchir le token JWT
     * POST /api/auth/refresh-token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Authorization") String token) {
        // Extraire le token du header "Bearer <token>"
        String jwt = token.substring(7);
        
        AuthResponse authResponse = authService.refreshToken(jwt);
        
        ApiResponse<AuthResponse> response = ApiResponse.success(
            "Token rafraîchi avec succès",
            authResponse
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Déconnexion
     * POST /api/auth/logout
     * Supprime le cookie JWT
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        // Supprimer le cookie JWT
        cookieUtil.deleteJwtCookie(response);
        
        ApiResponse<String> apiResponse = ApiResponse.success(
            "Déconnexion réussie. Cookie supprimé.",
            null
        );
        
        return ResponseEntity.ok(apiResponse);
    }
}
