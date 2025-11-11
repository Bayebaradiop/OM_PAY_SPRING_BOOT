package om.example.om_pay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Requête pour scanner un QR code et effectuer un paiement
 */
@Data
public class ScanQRRequest {
    
    @NotBlank(message = "Le code QR est obligatoire")
    private String codeQr;
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;
    
    private String description;
}
