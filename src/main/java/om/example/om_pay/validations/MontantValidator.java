package om.example.om_pay.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import om.example.om_pay.validations.annotations.ValidMontant;

public class MontantValidator implements ConstraintValidator<ValidMontant, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(ValidMontant constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double montant, ConstraintValidatorContext context) {
        if (montant == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "{error.validation.montant.required}"
            ).addConstraintViolation();
            return false;
        }

        if (montant <= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "{error.validation.montant.zero}"
            ).addConstraintViolation();
            return false;
        }

        if (montant < min) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "{error.validation.montant.min}"
            ).addConstraintViolation();
            return false;
        }

        if (montant > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "{error.validation.montant.max}"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
