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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeCompte;

@Entity
@Table(name = "compte")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
