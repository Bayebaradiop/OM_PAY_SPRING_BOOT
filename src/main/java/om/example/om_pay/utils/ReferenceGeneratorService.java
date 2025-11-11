package om.example.om_pay.utils;

import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.TransactionRepository;

/**
 * Service de génération de références uniques
 * Responsabilité : Création de références de transactions uniques
 */
@Service
public class ReferenceGeneratorService {

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;

    public ReferenceGeneratorService(
            TransactionRepository transactionRepository,
            CompteRepository compteRepository) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
    }

    /**
     * Génère une référence unique pour une transaction
     * Format: TRX + 8 caractères aléatoires en majuscules
     * Exemple: TRX4A2B9C1D
     */
    public String genererReference() {
        String reference;
        do {
            reference = "TRX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (transactionRepository.existsByReference(reference));

        return reference;
    }

    /**
     * Génère une référence avec préfixe selon le type de transaction
     * Format: [TYPE] + 8 caractères
     * Exemples: TRANS4A2B9C1D, DEPOT12345678, RETR87654321
     */
    public String genererReferenceAvecType(TypeTransaction typeTransaction) {
        String prefix;
        switch (typeTransaction) {
            case TRANSFERT:
                prefix = "TRANS";
                break;
            case DEPOT:
                prefix = "DEPOT";
                break;
            case RETRAIT:
                prefix = "RETR";
                break;
            case PAIEMENT:
                prefix = "PAY";
                break;
            default:
                prefix = "TRX";
        }

        String reference;
        do {
            reference = prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (transactionRepository.existsByReference(reference));

        return reference;
    }

    /**
     * Vérifie si une référence existe déjà
     */
    public boolean existeReference(String reference) {
        return transactionRepository.existsByReference(reference);
    }

    /**
     * Génère un numéro de compte unique
     * Format: 77 + 7 chiffres aléatoires
     * Exemple: 771234567
     */
    public String genererNumeroCompte() {
        String numeroCompte;
        do {
            numeroCompte = "77" + String.format("%07d", new Random().nextInt(10000000));
        } while (compteRepository.existsByNumeroCompte(numeroCompte));
        return numeroCompte;
    }
}
