package om.example.om_pay.validations;

import om.example.om_pay.validations.annotations.ValidNumeroCompte;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NumeroCompteValidator implements ConstraintValidator<ValidNumeroCompte, String> {

    // Pattern pour les numéros de compte Orange Money (OM suivi de 11 chiffres)
    private static final Pattern COMPTE_PATTERN = Pattern.compile("^OM[0-9]{11}$");

    @Override
    public void initialize(ValidNumeroCompte constraintAnnotation) {
        // Initialisation si nécessaire
    }

    @Override
    public boolean isValid(String numeroCompte, ConstraintValidatorContext context) {
        if (numeroCompte == null || numeroCompte.isEmpty()) {
            return false;
        }

        // Vérifier le format
        return COMPTE_PATTERN.matcher(numeroCompte).matches();
    }
}
