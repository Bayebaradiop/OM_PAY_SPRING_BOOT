package om.example.om_pay.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeCompte;

@Entity
@Table(name = "compte")
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroCompte;  // Ex: OM77123456789

    @Column(nullable = false)
    private Double solde = 0.0;

    private String devise = "XOF";  // Franc CFA

    @Enumerated(EnumType.STRING)
    private TypeCompte typeCompte; // PRINCIPAL, EPARGNE

    @Enumerated(EnumType.STRING)
    private Statut statut; // ACTIF, INACTIF

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification;

    // Relation avec User (propriétaire du compte)
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // Relations avec transactions
    @OneToMany(mappedBy = "compteExpediteur")
    private List<Transaction> transactionsEnvoyees;

    @OneToMany(mappedBy = "compteDestinataire")
    private List<Transaction> transactionsRecues;

    public Compte() {}

    // Getters & Setters
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

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
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

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<Transaction> getTransactionsEnvoyees() {
        return transactionsEnvoyees;
    }

    public void setTransactionsEnvoyees(List<Transaction> transactionsEnvoyees) {
        this.transactionsEnvoyees = transactionsEnvoyees;
    }

    public List<Transaction> getTransactionsRecues() {
        return transactionsRecues;
    }

    public void setTransactionsRecues(List<Transaction> transactionsRecues) {
        this.transactionsRecues = transactionsRecues;
    }
}
