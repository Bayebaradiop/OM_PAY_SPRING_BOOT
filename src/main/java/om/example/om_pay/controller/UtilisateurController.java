package om.example.om_pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import om.example.om_pay.dto.request.UpdateUtilisateurRequest;
import om.example.om_pay.dto.response.ApiResponse;
import om.example.om_pay.dto.response.UtilisateurResponse;
import om.example.om_pay.interfaces.IUtilisateurService;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private IUtilisateurService utilisateurService;

    /**
     * Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> updateUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUtilisateurRequest request) {
        UtilisateurResponse utilisateur = utilisateurService.updateUtilisateur(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur mis à jour avec succès", utilisateur));
    }

    /**
     * Bloquer un utilisateur
     */
    // Admin-only endpoints (getById, list, block/unblock, delete) removed to simplify API surface.

    /**
     * Changer le code PIN
     */
    @PutMapping("/change-pin")
    public ResponseEntity<ApiResponse<String>> changeCodePin(
            @RequestParam String telephone,
            @RequestParam String ancienPin,
            @RequestParam String nouveauPin) {
        utilisateurService.changeCodePin(telephone, ancienPin, nouveauPin);
        return ResponseEntity.ok(new ApiResponse<>(true, "Code PIN changé avec succès", null));
    }
}
