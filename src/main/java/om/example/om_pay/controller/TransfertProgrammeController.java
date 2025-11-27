package om.example.om_pay.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import om.example.om_pay.config.ApiResponse;
import om.example.om_pay.dto.request.TransfertProgrammeRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.model.TransfertProgramme;
import om.example.om_pay.service.TransfertProgrammeService;

@RestController
@RequestMapping("/api/transferts-programmes")
public class TransfertProgrammeController {

    private final TransfertProgrammeService transfertProgrammeService;

    public TransfertProgrammeController(TransfertProgrammeService transfertProgrammeService) {
        this.transfertProgrammeService = transfertProgrammeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> programmerTransfert(
            @Valid @RequestBody TransfertProgrammeRequest request) {
        
        TransactionResponse response = transfertProgrammeService.programmerTransfert(
            request.getTelephoneDestinataire(), 
            request.getMontant(), 
            request.getDateExecution()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Transfert programmé avec succès", response));
    }

    @GetMapping("/mes-transferts")
    public ResponseEntity<List<TransfertProgramme>> listerMesTransferts() {
        List<TransfertProgramme> transferts = transfertProgrammeService.listerMesTransfertsProgrammes();
        return ResponseEntity.ok(transferts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> annulerTransfert(@PathVariable Long id) {
        transfertProgrammeService.annulerTransfertProgramme(id);
        return ResponseEntity.noContent().build();
    }
}
