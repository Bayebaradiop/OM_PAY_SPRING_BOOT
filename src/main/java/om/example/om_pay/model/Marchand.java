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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.model.enums.Statut;

@Entity
@Table(name = "marchand")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Marchand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomCommercial;

    @Column(unique = true, nullable = false)
    private String numeroMarchand;

    @Column(unique = true, nullable = false)
    private String codeMarchand; 

    private String categorie;  
    private String adresse;
    private String email;

    @Enumerated(EnumType.STRING)
    private Statut statut;  

    @Column(nullable = false)
    private Double commission = 0.0; 

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "marchand")
    private List<Transaction> transactions;
}
