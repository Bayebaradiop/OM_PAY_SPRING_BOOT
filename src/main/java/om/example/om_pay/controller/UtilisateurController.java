package om.example.om_pay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import om.example.om_pay.dto.request.UpdateUtilisateurRequest;
import om.example.om_pay.dto.response.UtilisateurResponse;
import om.example.om_pay.service.IUtilisateurService;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final IUtilisateurService utilisateurService;

    public UtilisateurController(IUtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> updateUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUtilisateurRequest request) {
        UtilisateurResponse utilisateur = utilisateurService.updateUtilisateur(id, request);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Bloquer un utilisateur
     */
    // Admin-only endpoints (getById, list, block/unblock, delete) removed to simplify API surface.

    /**
     * Changer le code PIN
     */
    @PutMapping("/change-pin")
    public ResponseEntity<Void> changeCodePin(
            @RequestParam String telephone,
            @RequestParam String ancienPin,
            @RequestParam String nouveauPin) {
        utilisateurService.changeCodePin(telephone, ancienPin, nouveauPin);
        return ResponseEntity.ok().build();
    }
}
