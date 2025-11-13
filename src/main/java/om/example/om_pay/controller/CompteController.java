package om.example.om_pay.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import om.example.om_pay.dto.response.CompteResponse;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.ICompteService;

@RestController
@RequestMapping("/api/comptes")
public class CompteController {

    private final ICompteService compteService;
    private final UtilisateurRepository utilisateurRepository;

    public CompteController(ICompteService compteService, UtilisateurRepository utilisateurRepository) {
        this.compteService = compteService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Consulter le solde de l'utilisateur connecté
     * Récupère automatiquement le numéro de compte principal de l'utilisateur
     */
    @GetMapping("/solde")
    public ResponseEntity<Double> consulterSolde() {
        // Récupérer l'utilisateur connecté depuis le contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();
        
        // Trouver l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Récupérer le compte principal de l'utilisateur
        Compte compte = compteService.getComptePrincipal(utilisateur.getId());
        
        // Consulter le solde avec le numéro de compte récupéré
        Double solde = compteService.consulterSolde(compte.getNumeroCompte());
        
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
