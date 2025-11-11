package om.example.om_pay.service.calculation;

import org.springframework.stereotype.Service;

import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.UtilisateurRepository;

/**
 * Service de gestion des plafonds quotidiens
 * Responsabilité : Vérification et mise à jour des plafonds de transfert
 */
@Service
public class PlafondService {

    private final UtilisateurRepository utilisateurRepository;

    public PlafondService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Vérifie que le plafond quotidien n'est pas dépassé
     */
    public void verifierPlafond(Utilisateur utilisateur, Double montant) {
        Double totalTransfertJour = utilisateur.getTotalTransfertJour();
        Double plafondQuotidien = utilisateur.getPlafondQuotidien();

        if (totalTransfertJour + montant > plafondQuotidien) {
            throw new BadRequestException(
                String.format("Plafond quotidien dépassé. Utilisé: %.2f FCFA, Plafond: %.2f FCFA", 
                    totalTransfertJour, plafondQuotidien)
            );
        }
    }

    /**
     * Incrémente le total des transferts du jour
     */
    public void incrementerTransfertJour(Utilisateur utilisateur, Double montant) {
        Double nouveauTotal = utilisateur.getTotalTransfertJour() + montant;
        utilisateur.setTotalTransfertJour(nouveauTotal);
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Réinitialise le total des transferts du jour (à appeler via un scheduler quotidien)
     */
    public void reinitialiserPlafondJournalier(Utilisateur utilisateur) {
        utilisateur.setTotalTransfertJour(0.0);
        utilisateurRepository.save(utilisateur);
    }
}
