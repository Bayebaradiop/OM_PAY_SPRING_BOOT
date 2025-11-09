package om.example.om_pay.validations;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import om.example.om_pay.validations.annotations.ValidCodePin;

public class CodePinValidator implements ConstraintValidator<ValidCodePin, String> {

    private static final Pattern PIN_PATTERN = Pattern.compile("^[0-9]{6}$");

    @Override
    public void initialize(ValidCodePin constraintAnnotation) {
        // Initialisation si nécessaire
    }

    @Override
    public boolean isValid(String codePin, ConstraintValidatorContext context) {
        if (codePin == null || codePin.isEmpty()) {
            return false;
        }

        // Vérifier le format
        if (!PIN_PATTERN.matcher(codePin).matches()) {
            return false;
        }

        // Vérifier que ce n'est pas un PIN faible (ex: 000000, 111111, 123456, etc.)
        if (isWeakPin(codePin)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "{error.validation.codepin.weak}"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isWeakPin(String pin) {
        // PINs faibles courants
        String[] weakPins = {
            "000000", "111111", "222222", "333333", "444444", 
            "555555", "666666", "777777", "888888", "999999",
            "123456", "654321", "111222", "000111", "121212"
        };

        for (String weakPin : weakPins) {
            if (pin.equals(weakPin)) {
                return true;
            }
        }

        return false;
    }
}
