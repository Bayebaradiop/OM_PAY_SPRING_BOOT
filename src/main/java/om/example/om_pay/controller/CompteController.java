package om.example.om_pay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import om.example.om_pay.dto.response.ApiResponse;
import om.example.om_pay.dto.response.CompteResponse;
import om.example.om_pay.interfaces.ICompteService;

@RestController
@RequestMapping("/api/comptes")
public class CompteController {

    @Autowired
    private ICompteService compteService;

    /**
     * Consulter le solde d'un compte
     */
    @GetMapping("/solde/{numeroCompte}")
    public ResponseEntity<ApiResponse<Double>> consulterSolde(@PathVariable String numeroCompte) {
        Double solde = compteService.consulterSolde(numeroCompte);
        return ResponseEntity.ok(new ApiResponse<>(true, "Solde récupéré avec succès", solde));
    }

    /**
     * Récupérer tous les comptes d'un utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<ApiResponse<List<CompteResponse>>> getComptesByUtilisateur(@PathVariable Long utilisateurId) {
        List<CompteResponse> comptes = compteService.getComptesByUtilisateur(utilisateurId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comptes récupérés avec succès", comptes));
    }

    /**
     * Bloquer un compte
     */
    // Admin-only endpoints (bloquer/debloquer) removed to simplify API surface.
}
