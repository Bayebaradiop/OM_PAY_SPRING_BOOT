package om.example.om_pay.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;  // Ex: TRX20251107123456

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction typeTransaction; // PAIEMENT, DEPOT, RETRAIT, TRANSFERT

    @Column(nullable = false)
    private Double montant;

    private Double frais = 0.0;

    @Column(nullable = false)
    private Double montantTotal;  // montant + frais

    // Compte expéditeur (peut être null pour DEPOT)
    @ManyToOne
    @JoinColumn(name = "compte_expediteur_id")
    private Compte compteExpediteur;

    // Compte destinataire (peut être null pour RETRAIT)
    @ManyToOne
    @JoinColumn(name = "compte_destinataire_id")
    private Compte compteDestinataire;

    // Pour paiement marchand
    @ManyToOne
    @JoinColumn(name = "marchand_id")
    private Marchand marchand;

    // Distributeur qui effectue l'opération (pour DEPOT/RETRAIT)
    @ManyToOne
    @JoinColumn(name = "distributeur_id")
    private Utilisateur distributeur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransaction statut; // EN_COURS, REUSSI, ECHOUE, ANNULE

    private String description;
    private String messageErreur;  // Si transaction échouée

    @Column(nullable = false)
    private LocalDateTime dateTransaction = LocalDateTime.now();

    private LocalDateTime dateTraitement;  // Quand la transaction a été complétée

    public Transaction() {}

    // Getters & Setters
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

    public Compte getCompteExpediteur() {
        return compteExpediteur;
    }

    public void setCompteExpediteur(Compte compteExpediteur) {
        this.compteExpediteur = compteExpediteur;
    }

    public Compte getCompteDestinataire() {
        return compteDestinataire;
    }

    public void setCompteDestinataire(Compte compteDestinataire) {
        this.compteDestinataire = compteDestinataire;
    }

    public Marchand getMarchand() {
        return marchand;
    }

    public void setMarchand(Marchand marchand) {
        this.marchand = marchand;
    }

    public Utilisateur getDistributeur() {
        return distributeur;
    }

    public void setDistributeur(Utilisateur distributeur) {
        this.distributeur = distributeur;
    }

    public StatutTransaction getStatut() {
        return statut;
    }

    public void setStatut(StatutTransaction statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessageErreur() {
        return messageErreur;
    }

    public void setMessageErreur(String messageErreur) {
        this.messageErreur = messageErreur;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
}
