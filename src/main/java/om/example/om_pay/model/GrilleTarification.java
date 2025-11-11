package om.example.om_pay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeTransaction;

@Entity
@Table(name = "grille_tarification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrilleTarification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction typeTransaction;

    @Column(nullable = false)
    private Double montantMin;

    @Column(nullable = false)
    private Double montantMax;

    @Column(nullable = false)
    private Double frais;

    private Boolean pourcentage = false;  // Si true, frais est un %

    @Enumerated(EnumType.STRING)
    private Statut statut;
}
