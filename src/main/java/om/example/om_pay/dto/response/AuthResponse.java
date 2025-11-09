package om.example.om_pay.dto.response;

import om.example.om_pay.model.enums.Role;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String telephone;
    private String nom;
    private String prenom;
    private Role role;

    // Constructeurs
    public AuthResponse() {
    }

    public AuthResponse(String token, String telephone, String nom, String prenom, Role role) {
        this.token = token;
        this.telephone = telephone;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    // Getters et Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
