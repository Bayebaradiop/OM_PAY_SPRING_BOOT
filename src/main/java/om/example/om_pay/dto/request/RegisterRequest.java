package om.example.om_pay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.validations.annotations.ValidCodePin;
import om.example.om_pay.validations.annotations.ValidTelephone;

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

    @ValidCodePin
    private String codePin;

    @NotNull(message = "{error.validation.role.required}")
    private Role role;

    // Constructeurs
    public RegisterRequest() {
    }

    public RegisterRequest(String nom, String prenom, String telephone, String email, 
                          String motDePasse, String codePin, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.motDePasse = motDePasse;
        this.codePin = codePin;
        this.role = role;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getCodePin() {
        return codePin;
    }

    public void setCodePin(String codePin) {
        this.codePin = codePin;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
