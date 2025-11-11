package om.example.om_pay.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import om.example.om_pay.dto.response.CompteResponse;
import om.example.om_pay.service.ICompteService;

@RestController
@RequestMapping("/api/comptes")
public class CompteController {

    private final ICompteService compteService;

    public CompteController(ICompteService compteService) {
        this.compteService = compteService;
    }

    /**
     * Consulter le solde d'un compte
     */
    @GetMapping("/solde/{numeroCompte}")
    public ResponseEntity<Double> consulterSolde(@PathVariable String numeroCompte) {
        Double solde = compteService.consulterSolde(numeroCompte);
        return ResponseEntity.ok(solde);
    }

    /**
     * Récupérer tous les comptes d'un utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<CompteResponse>> getComptesByUtilisateur(@PathVariable Long utilisateurId) {
        List<CompteResponse> comptes = compteService.getComptesByUtilisateur(utilisateurId);
        return ResponseEntity.ok(comptes);
    }

}
