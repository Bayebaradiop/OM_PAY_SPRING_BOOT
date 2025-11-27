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
import om.example.om_pay.model.enums.StatutTransfertProgramme;

@Entity
@Table(name = "transfert_programme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransfertProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_expediteur_id", nullable = false)
    private Utilisateur utilisateurExpediteur;

    private String telephoneDestinataire;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private LocalDateTime dateExecution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransfertProgramme statut;

    private LocalDateTime dateCreation = LocalDateTime.now();
    
    private LocalDateTime dateExecutionReelle;
    
    private String messageErreur;
}
