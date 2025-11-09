package om.example.om_pay.dto.request;

import jakarta.validation.constraints.Email;

public class UpdateUtilisateurRequest {

    private String nom;
    private String prenom;

    @Email(message = "{error.validation.email.invalid}")
    private String email;

    // Constructeurs
    public UpdateUtilisateurRequest() {
    }

    public UpdateUtilisateurRequest(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
