package om.example.om_pay.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.validations.annotations.ValidTelephone;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @ValidTelephone
    private String telephone;

    @NotBlank(message = "{error.validation.motDePasse.required}")
    private String motDePasse;
}
