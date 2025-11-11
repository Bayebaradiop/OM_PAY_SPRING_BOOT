package om.example.om_pay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la vérification du code secret lors de la première connexion
 */
public class VerifyCodeSecretRequest {

    @NotBlank(message = "Le téléphone est requis")
    private String telephone;

    @NotBlank(message = "Le code secret est requis")
    @Size(min = 6, max = 6, message = "Le code secret doit contenir exactement 6 caractères")
    private String codeSecret;

    public VerifyCodeSecretRequest() {}

    public VerifyCodeSecretRequest(String telephone, String codeSecret) {
        this.telephone = telephone;
        this.codeSecret = codeSecret;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCodeSecret() {
        return codeSecret;
    }

    public void setCodeSecret(String codeSecret) {
        this.codeSecret = codeSecret;
    }
}
