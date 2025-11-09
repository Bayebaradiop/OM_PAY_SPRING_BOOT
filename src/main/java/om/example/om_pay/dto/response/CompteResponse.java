package om.example.om_pay.dto.response;

import java.time.LocalDateTime;

import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeCompte;

public class CompteResponse {

    private Long id;
    private String numeroCompte;
    private Double solde;
    private TypeCompte typeCompte;
    private Statut statut;
    private String telephoneProprietaire;
    private String nomProprietaire;
    private LocalDateTime dateCreation;

    // Constructeurs
    public CompteResponse() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

    public TypeCompte getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(TypeCompte typeCompte) {
        this.typeCompte = typeCompte;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getTelephoneProprietaire() {
        return telephoneProprietaire;
    }

    public void setTelephoneProprietaire(String telephoneProprietaire) {
        this.telephoneProprietaire = telephoneProprietaire;
    }

    public String getNomProprietaire() {
        return nomProprietaire;
    }

    public void setNomProprietaire(String nomProprietaire) {
        this.nomProprietaire = nomProprietaire;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
