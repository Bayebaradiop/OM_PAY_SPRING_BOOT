package om.example.om_pay.dto.request;

import jakarta.validation.constraints.NotBlank;
import om.example.om_pay.validations.annotations.ValidTelephone;

public class LoginRequest {

    @ValidTelephone
    private String telephone;

    @NotBlank(message = "{error.validation.motDePasse.required}")
    private String motDePasse;

    // Constructeurs
    public LoginRequest() {
    }

    public LoginRequest(String telephone, String motDePasse) {
        this.telephone = telephone;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
