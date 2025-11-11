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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.model.enums.StatutTransaction;
import om.example.om_pay.model.enums.TypeTransaction;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
