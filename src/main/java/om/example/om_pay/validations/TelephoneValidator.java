package om.example.om_pay.validations;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import om.example.om_pay.validations.annotations.ValidTelephone;

public class TelephoneValidator implements ConstraintValidator<ValidTelephone, String> {

    // Pattern pour les numéros de téléphone sénégalais
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^(77|78|76|70|75)[0-9]{7}$");

    @Override
    public void initialize(ValidTelephone constraintAnnotation) {
        // Initialisation si nécessaire
    }

    @Override
    public boolean isValid(String telephone, ConstraintValidatorContext context) {
        if (telephone == null || telephone.isEmpty()) {
            return false;
        }

        // Nettoyer le numéro (enlever espaces, +221, etc.)
        String cleanedTelephone = telephone.replaceAll("[\\s\\-\\+]", "");
        
        // Si commence par 221 (code pays Sénégal), l'enlever
        if (cleanedTelephone.startsWith("221")) {
            cleanedTelephone = cleanedTelephone.substring(3);
        }

        return TELEPHONE_PATTERN.matcher(cleanedTelephone).matches();
    }
}
