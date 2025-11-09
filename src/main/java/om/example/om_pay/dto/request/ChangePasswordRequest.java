package om.example.om_pay.dto.request;

import jakarta.validation.constraints.NotBlank;
import om.example.om_pay.validations.annotations.ValidTelephone;

public class ChangePasswordRequest {

    @ValidTelephone
    private String telephone;

    @NotBlank(message = "{error.validation.motDePasse.required}")
    private String ancienMotDePasse;

    @NotBlank(message = "{error.validation.motDePasse.required}")
    private String nouveauMotDePasse;

    // Constructeurs
    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String telephone, String ancienMotDePasse, String nouveauMotDePasse) {
        this.telephone = telephone;
        this.ancienMotDePasse = ancienMotDePasse;
        this.nouveauMotDePasse = nouveauMotDePasse;
    }

    // Getters et Setters
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAncienMotDePasse() {
        return ancienMotDePasse;
    }

    public void setAncienMotDePasse(String ancienMotDePasse) {
        this.ancienMotDePasse = ancienMotDePasse;
    }

    public String getNouveauMotDePasse() {
        return nouveauMotDePasse;
    }

    public void setNouveauMotDePasse(String nouveauMotDePasse) {
        this.nouveauMotDePasse = nouveauMotDePasse;
    }
}
