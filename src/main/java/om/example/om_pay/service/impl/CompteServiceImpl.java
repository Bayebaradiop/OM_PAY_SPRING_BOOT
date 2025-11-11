package om.example.om_pay.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.response.CompteResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.service.ICompteService;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.utils.ReferenceGeneratorService;
import om.example.om_pay.validations.AccountValidationService;

@Service
public class CompteServiceImpl implements ICompteService {

    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ReferenceGeneratorService referenceGenerator;
    private final AccountValidationService accountValidation;

    public CompteServiceImpl(
            CompteRepository compteRepository,
            UtilisateurRepository utilisateurRepository,
            ReferenceGeneratorService referenceGenerator,
            AccountValidationService accountValidation) {
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.referenceGenerator = referenceGenerator;
        this.accountValidation = accountValidation;
    }

    @Override
    public Double consulterSolde(String numeroCompte) {
        Compte compte = accountValidation.getCompteWithPermission(numeroCompte);
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
                .map(CompteResponse::fromCompte)
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
        compte.setNumeroCompte(referenceGenerator.genererNumeroCompte());
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
        Compte compte = accountValidation.getCompteWithPermission(numeroCompte);
        compte.setStatut(Statut.INACTIF);
        compteRepository.save(compte);
    }

    @Override
    @Transactional
    public void debloquer(String numeroCompte) {
        Compte compte = accountValidation.getCompteWithPermission(numeroCompte);
        compte.setStatut(Statut.ACTIF);
        compteRepository.save(compte);
    }
}
