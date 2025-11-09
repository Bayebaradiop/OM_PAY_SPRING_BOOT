package om.example.om_pay.dto.response;

import java.time.LocalDateTime;

import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;

public class TransactionResponse {

    private Long id;
    private String reference;
    private TypeTransaction typeTransaction;
    private Double montant;
    private Double frais;
    private Double montantTotal;
    private StatutTransaction statut;
    private String compteExpediteur;
    private String compteDestinataire;
    private String telephoneDistributeur;
    private String nomMarchand;
    private String description;
    private LocalDateTime dateCreation;
    private Double nouveauSolde; // Nouveau solde de l'expéditeur après transaction

    // Constructeurs
    public TransactionResponse() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Double getFrais() {
        return frais;
    }

    public void setFrais(Double frais) {
        this.frais = frais;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public StatutTransaction getStatut() {
        return statut;
    }

    public void setStatut(StatutTransaction statut) {
        this.statut = statut;
    }

    public String getCompteExpediteur() {
        return compteExpediteur;
    }

    public void setCompteExpediteur(String compteExpediteur) {
        this.compteExpediteur = compteExpediteur;
    }

    public String getCompteDestinataire() {
        return compteDestinataire;
    }

    public void setCompteDestinataire(String compteDestinataire) {
        this.compteDestinataire = compteDestinataire;
    }

    public String getTelephoneDistributeur() {
        return telephoneDistributeur;
    }

    public void setTelephoneDistributeur(String telephoneDistributeur) {
        this.telephoneDistributeur = telephoneDistributeur;
    }

    public String getNomMarchand() {
        return nomMarchand;
    }

    public void setNomMarchand(String nomMarchand) {
        this.nomMarchand = nomMarchand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Double getNouveauSolde() {
        return nouveauSolde;
    }

    public void setNouveauSolde(Double nouveauSolde) {
        this.nouveauSolde = nouveauSolde;
    }
}
