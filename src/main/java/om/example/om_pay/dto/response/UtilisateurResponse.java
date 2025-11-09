package om.example.om_pay.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import om.example.om_pay.model.enums.Role;
import om.example.om_pay.model.enums.Statut;

public class UtilisateurResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private Role role;
    private Statut statut;
    private Double plafondQuotidien;
    private Double totalTransfertJour;
    private List<CompteResponse> comptes;
    private LocalDateTime dateCreation;

    // Constructeurs
    public UtilisateurResponse() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Double getPlafondQuotidien() {
        return plafondQuotidien;
    }

    public void setPlafondQuotidien(Double plafondQuotidien) {
        this.plafondQuotidien = plafondQuotidien;
    }

    public Double getTotalTransfertJour() {
        return totalTransfertJour;
    }

    public void setTotalTransfertJour(Double totalTransfertJour) {
        this.totalTransfertJour = totalTransfertJour;
    }

    public List<CompteResponse> getComptes() {
        return comptes;
    }

    public void setComptes(List<CompteResponse> comptes) {
        this.comptes = comptes;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
