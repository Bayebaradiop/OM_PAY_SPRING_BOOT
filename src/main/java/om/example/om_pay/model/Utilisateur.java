package om.example.om_pay.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.model.enums.Statut;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String telephone;

    private String email;

    @Column(length = 255, nullable = false)
    private String motDePasse; 

    @Column(length = 255, nullable = true)
    private String codePin;  // Code PIN optionnel (nullable)

    @Enumerated(EnumType.STRING)
    private Role role;  

    @Enumerated(EnumType.STRING)
    private Statut statut; 

    private Double plafondQuotidien = 500000.0;  // Limite par jour
    private Double totalTransfertJour = 0.0;     // Suivi des transferts du jour
    private LocalDate dernierResetPlafond;       // Pour réinitialiser chaque jour

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Compte> comptes;

    @OneToMany(mappedBy = "distributeur")
    private List<Transaction> operationsDistributeur;

    public Utilisateur() {}

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

    public LocalDate getDernierResetPlafond() {
        return dernierResetPlafond;
    }

    public void setDernierResetPlafond(LocalDate dernierResetPlafond) {
        this.dernierResetPlafond = dernierResetPlafond;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public List<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
    }

    public List<Transaction> getOperationsDistributeur() {
        return operationsDistributeur;
    }

    public void setOperationsDistributeur(List<Transaction> operationsDistributeur) {
        this.operationsDistributeur = operationsDistributeur;
    }
}
