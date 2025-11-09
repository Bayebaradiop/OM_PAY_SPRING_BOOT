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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import om.example.om_pay.model.enums.Statut;

@Entity
@Table(name = "marchand")
public class Marchand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomCommercial;

    @Column(unique = true, nullable = false)
    private String numeroMarchand;  // Ex: 77123456789

    @Column(unique = true, nullable = false)
    private String codeMarchand;  // Ex: OM1234

    private String categorie;  // Restaurant, Boutique, Pharmacie, etc.
    private String adresse;
    private String email;

    @Enumerated(EnumType.STRING)
    private Statut statut;  // ACTIF, INACTIF

    @Column(nullable = false)
    private Double commission = 0.0;  // % de commission sur chaque paiement

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "marchand")
    private List<Transaction> transactions;

    public Marchand() {}

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomCommercial() {
        return nomCommercial;
    }

    public void setNomCommercial(String nomCommercial) {
        this.nomCommercial = nomCommercial;
    }

    public String getNumeroMarchand() {
        return numeroMarchand;
    }

    public void setNumeroMarchand(String numeroMarchand) {
        this.numeroMarchand = numeroMarchand;
    }

    public String getCodeMarchand() {
        return codeMarchand;
    }

    public void setCodeMarchand(String codeMarchand) {
        this.codeMarchand = codeMarchand;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
