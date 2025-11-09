package om.example.om_pay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.UpdateUtilisateurRequest;
import om.example.om_pay.dto.response.UtilisateurResponse;
import om.example.om_pay.exception.ResourceNotFoundException;
import om.example.om_pay.exception.UnauthorizedException;
import om.example.om_pay.interfaces.IUtilisateurService;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.UtilisateurRepository;

@Service
public class UtilisateurServiceImpl implements IUtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Utilisateur getById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    @Override
    public Utilisateur getByTelephone(String telephone) {
        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    @Override
    public Utilisateur getCurrentUser() {
        return getCurrentUserEntity();
    }

    @Override
    @Transactional
    public UtilisateurResponse updateUtilisateur(Long id, UpdateUtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Mettre à jour les informations
        if (request.getNom() != null && !request.getNom().isEmpty()) {
            utilisateur.setNom(request.getNom());
        }

        if (request.getPrenom() != null && !request.getPrenom().isEmpty()) {
            utilisateur.setPrenom(request.getPrenom());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            utilisateur.setEmail(request.getEmail());
        }

        utilisateur = utilisateurRepository.save(utilisateur);
        return mapToResponse(utilisateur);
    }

    @Override
    @Transactional
    public void changeStatut(Long id, om.example.om_pay.model.enums.Statut statut) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        utilisateur.setStatut(statut);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public void bloquer(Long id) {
        changeStatut(id, om.example.om_pay.model.enums.Statut.INACTIF);
    }

    @Override
    @Transactional
    public void debloquer(Long id) {
        changeStatut(id, om.example.om_pay.model.enums.Statut.ACTIF);
    }

    @Override
    public java.util.List<UtilisateurResponse> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        utilisateurRepository.delete(utilisateur);
    }

    @Override
    public boolean verifyCodePin(String telephone, String codePin) {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return passwordEncoder.matches(codePin, utilisateur.getCodePin());
    }

    @Override
    @Transactional
    public void changeCodePin(String telephone, String ancienPin, String nouveauPin) {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier l'ancien PIN
        if (!passwordEncoder.matches(ancienPin, utilisateur.getCodePin())) {
            throw new UnauthorizedException("Ancien code PIN incorrect");
        }

        // Changer le PIN
        utilisateur.setCodePin(passwordEncoder.encode(nouveauPin));
        utilisateurRepository.save(utilisateur);
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Récupère l'utilisateur connecté (entité)
     */
    private Utilisateur getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String telephone = authentication.getName();

        return utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));
    }

    /**
     * Convertit un Utilisateur en UtilisateurResponse
     */
    private UtilisateurResponse mapToResponse(Utilisateur utilisateur) {
        UtilisateurResponse response = new UtilisateurResponse();
        response.setId(utilisateur.getId());
        response.setNom(utilisateur.getNom());
        response.setPrenom(utilisateur.getPrenom());
        response.setTelephone(utilisateur.getTelephone());
        response.setEmail(utilisateur.getEmail());
        response.setRole(utilisateur.getRole());
        response.setStatut(utilisateur.getStatut());
        response.setDateCreation(utilisateur.getDateCreation());
        return response;
    }
}
