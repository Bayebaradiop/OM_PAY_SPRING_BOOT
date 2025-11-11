package om.example.om_pay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import om.example.om_pay.config.ApiResponse;
import om.example.om_pay.dto.request.ScanQRRequest;
import om.example.om_pay.dto.response.QRCodeResponse;
import om.example.om_pay.service.QRCodeService;

/**
 * Contrôleur REST pour la gestion des QR codes
 */
@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
@Tag(name = "QR Code", description = "Gestion des codes QR pour paiements")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @GetMapping("/mon-qrcode")
    @Operation(
        summary = "Récupérer mon QR code",
        description = "Retourne le QR code personnel de l'utilisateur connecté",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<QRCodeResponse>> getMonQRCode() {
        QRCodeResponse response = qrCodeService.getMonQRCode();
        return ResponseEntity.ok(ApiResponse.success("QR code récupéré avec succès", response));
    }

    @PostMapping("/scan")
    @Operation(
        summary = "Scanner et payer via QR code",
        description = "Scanne un QR code et effectue le paiement",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Void>> scannerEtPayer(
            @Valid @RequestBody ScanQRRequest request) {
        
        qrCodeService.scannerEtPayer(request);
        return ResponseEntity.ok(ApiResponse.success("Paiement effectué avec succès", null));
    }
}
