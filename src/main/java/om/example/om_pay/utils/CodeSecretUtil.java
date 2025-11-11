package om.example.om_pay.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

/**
 * Utilitaire pour la génération et la gestion des codes secrets
 */
@Component
public class CodeSecretUtil {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();
    
    /**
     * Génère un code secret aléatoire de 6 caractères (lettres majuscules + chiffres)
     * Exemple: A3K9L2, X7M4P1, etc.
     */
    public String genererCodeSecret() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
    
    /**
     * Calcule la date d'expiration du code secret
     * @param minutes Durée de validité en minutes
     * @return Date d'expiration
     */
    public LocalDateTime calculerExpiration(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }
    
    /**
     * Vérifie si un code secret est expiré
     * @param expiration Date d'expiration du code
     * @return true si expiré, false sinon
     */
    public boolean estExpire(LocalDateTime expiration) {
        return expiration != null && LocalDateTime.now().isAfter(expiration);
    }
}
