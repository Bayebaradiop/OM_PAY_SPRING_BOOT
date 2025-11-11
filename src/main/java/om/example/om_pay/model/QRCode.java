package om.example.om_pay.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un QR Code associé à un compte
 * Un compte = Un QR code unique
 */
@Entity
@Table(name = "qr_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String codeQr; // Le contenu du QR (UUID simplifié)
    
    @OneToOne
    @JoinColumn(name = "compte_id", nullable = false, unique = true)
    private Compte compte; // Un QR par compte
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    private LocalDateTime dateUtilisation; // Dernière utilisation
    
    private Integer nombreUtilisations = 0;
}
