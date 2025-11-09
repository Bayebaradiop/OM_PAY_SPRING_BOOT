package om.example.om_pay.validations.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import om.example.om_pay.validations.MontantValidator;

@Documented
@Constraint(validatedBy = MontantValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMontant {
    
    String message() default "{error.validation.montant.invalid}";
    
    double min() default 1.0;
    
    double max() default 1000000.0;
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
