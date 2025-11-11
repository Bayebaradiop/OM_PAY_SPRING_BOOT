package om.example.om_pay.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse contenant les informations du QR code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeResponse {
    
    private Long id;
    private String codeQr;
    private String qrCodeImage; // Image Base64 du QR code
    private String numeroCompte;
    private String nomComplet;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private Integer nombreUtilisations;
}
