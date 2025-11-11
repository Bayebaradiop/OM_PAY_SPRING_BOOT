package om.example.om_pay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.validations.annotations.ValidTelephone;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "{error.validation.nom.required}")
    private String nom;

    @NotBlank(message = "{error.validation.prenom.required}")
    private String prenom;

    @ValidTelephone
    private String telephone;

    @Email(message = "{error.validation.email.invalid}")
    @NotBlank(message = "{error.validation.email.required}")
    private String email;

    @NotBlank(message = "{error.validation.motDePasse.required}")
    private String motDePasse;

    // Code PIN optionnel - peut être défini plus tard via un endpoint dédié
    // @ValidCodePin - Validation désactivée, le code PIN n'est plus requis
    private String codePin;

    @NotNull(message = "{error.validation.role.required}")
    private Role role;
}
