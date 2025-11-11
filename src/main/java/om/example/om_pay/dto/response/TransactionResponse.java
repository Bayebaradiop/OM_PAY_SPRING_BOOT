package om.example.om_pay.dto.response;

import java.time.LocalDateTime;

import om.example.om_pay.model.Transaction;
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

    // Méthode factory pour créer un TransactionResponse depuis une Transaction
    public static TransactionResponse fromTransaction(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        
        response.setId(transaction.getId());
        response.setReference(transaction.getReference());
        response.setTypeTransaction(transaction.getTypeTransaction());
        response.setMontant(transaction.getMontant());
        response.setFrais(transaction.getFrais());
        response.setMontantTotal(transaction.getMontantTotal());
        response.setStatut(transaction.getStatut());
        response.setDateCreation(transaction.getDateTransaction());

        // Informations du compte expéditeur
        if (transaction.getCompteExpediteur() != null) {
            response.setCompteExpediteur(transaction.getCompteExpediteur().getNumeroCompte());
            response.setNouveauSolde(transaction.getCompteExpediteur().getSolde());
        }

        // Informations du compte destinataire
        if (transaction.getCompteDestinataire() != null) {
            response.setCompteDestinataire(transaction.getCompteDestinataire().getNumeroCompte());
        }

        // Informations du distributeur (pour dépôts et retraits)
        if (transaction.getDistributeur() != null) {
            response.setTelephoneDistributeur(transaction.getDistributeur().getTelephone());
        }

        // Informations du marchand (pour paiements)
        if (transaction.getMarchand() != null) {
            response.setNomMarchand(transaction.getMarchand().getNomCommercial());
        }

        return response;
    }
}
