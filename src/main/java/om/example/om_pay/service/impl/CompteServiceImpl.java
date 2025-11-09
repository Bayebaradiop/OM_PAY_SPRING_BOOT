package om.example.om_pay.service.impl;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.response.CompteResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.interfaces.ICompteService;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;

@Service
public class CompteServiceImpl implements ICompteService {

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public Double consulterSolde(String numeroCompte) {
        Compte compte = getCompteWithPermission(numeroCompte);
        return compte.getSolde();
    }

    @Override
    public Compte getByNumeroCompte(String numeroCompte) {
        return compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
    }

    @Override
    public Compte getById(Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompteResponse> getComptesByUtilisateur(Long utilisateurId) {
        List<Compte> comptes = compteRepository.findByUtilisateurId(utilisateurId);
        return comptes.stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Compte getComptePrincipal(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return utilisateur.getComptes().stream()
                .filter(c -> c.getTypeCompte().name().equals("PRINCIPAL"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Compte principal non trouvé"));
    }

    @Override
    public Compte creerCompte(Long utilisateurId, om.example.om_pay.model.enums.TypeCompte typeCompte) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Compte compte = new Compte();
        compte.setNumeroCompte(genererNumeroCompte());
        compte.setTypeCompte(typeCompte);
        compte.setSolde(0.0);
        compte.setStatut(Statut.ACTIF);
        compte.setUtilisateur(utilisateur);

        return compteRepository.save(compte);
    }

    @Override
    @Transactional
    public void crediter(String numeroCompte, Double montant) {
        if (montant <= 0) {
            throw new BadRequestException("Le montant doit être positif");
        }

        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        compte.setSolde(compte.getSolde() + montant);
        compteRepository.save(compte);
    }

    @Override
    @Transactional
    public void debiter(String numeroCompte, Double montant) {
        if (montant <= 0) {
            throw new BadRequestException("Le montant doit être positif");
        }

        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        if (compte.getSolde() < montant) {
            throw new BadRequestException("Solde insuffisant");
        }

        compte.setSolde(compte.getSolde() - montant);
        compteRepository.save(compte);
    }

    @Override
    @Transactional
    public void bloquer(String numeroCompte) {
        Compte compte = getCompteWithPermission(numeroCompte);
        compte.setStatut(Statut.INACTIF);
        compteRepository.save(compte);
    }

    @Override
    @Transactional
    public void debloquer(String numeroCompte) {
        Compte compte = getCompteWithPermission(numeroCompte);
        compte.setStatut(Statut.ACTIF);
        compteRepository.save(compte);
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Récupère un compte et vérifie que l'utilisateur connecté est le propriétaire
     */
    private Compte getCompteWithPermission(String numeroCompte) {
        Utilisateur currentUser = getCurrentUser();
        
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        // Vérifier que le compte appartient bien à l'utilisateur
        if (!compte.getUtilisateur().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Accès non autorisé à ce compte");
        }

        return compte;
    }

    /**
     * Récupère l'utilisateur connecté
     */
    private Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();

        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));
    }

    /**
     * Convertit un Compte en CompteResponse
     */
    private CompteResponse mapToResponse(Compte compte) {
        CompteResponse response = new CompteResponse();
        response.setId(compte.getId());
        response.setNumeroCompte(compte.getNumeroCompte());
        response.setTypeCompte(compte.getTypeCompte());
        response.setSolde(compte.getSolde());
        response.setStatut(compte.getStatut());
        return response;
    }

    /**
     * Génère un numéro de compte unique
     */
    private String genererNumeroCompte() {
        String numeroCompte;
        do {
            numeroCompte = "77" + String.format("%07d", new Random().nextInt(10000000));
        } while (compteRepository.existsByNumeroCompte(numeroCompte));
        return numeroCompte;
    }
}
