package om.example.om_pay.service.calculation;

import org.springframework.stereotype.Service;

import om.example.om_pay.model.enums.TypeTransaction;

/**
 * Service de calcul des frais de transaction
 * Responsabilité : Centralise toute la logique de calcul des frais
 */
@Service
public class FraisCalculService {

    // Taux de frais pour les transferts et paiements (0.85%)
    private static final Double TAUX_FRAIS_TRANSFERT = 0.0085;
    private static final Double TAUX_FRAIS_PAIEMENT = 0.0085;

    /**
     * Calcule les frais selon le type de transaction
     */
    public Double calculerFrais(TypeTransaction typeTransaction, Double montant) {
        switch (typeTransaction) {
            case TRANSFERT:
                return montant * TAUX_FRAIS_TRANSFERT; // 0.85%
            case PAIEMENT:
                return montant * TAUX_FRAIS_PAIEMENT; // 0.85%
            case RETRAIT:
            case DEPOT:
                return 0.0; // Aucun frais
            default:
                return 0.0;
        }
    }

    /**
     * Calcule le montant total (montant + frais)
     */
    public Double calculerMontantTotal(Double montant, Double frais) {
        return montant + frais;
    }

    /**
     * Calcule frais et montant total en une seule fois
     */
    public FraisResult calculerFraisEtTotal(TypeTransaction typeTransaction, Double montant) {
        Double frais = calculerFrais(typeTransaction, montant);
        Double montantTotal = calculerMontantTotal(montant, frais);
        return new FraisResult(frais, montantTotal);
    }

    /**
     * Classe interne pour retourner frais + montant total
     */
    public static class FraisResult {
        private final Double frais;
        private final Double montantTotal;

        public FraisResult(Double frais, Double montantTotal) {
            this.frais = frais;
            this.montantTotal = montantTotal;
        }

        public Double getFrais() {
            return frais;
        }

        public Double getMontantTotal() {
            return montantTotal;
        }
    }
}
