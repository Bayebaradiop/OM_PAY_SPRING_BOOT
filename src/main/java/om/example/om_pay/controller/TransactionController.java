package om.example.om_pay.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import om.example.om_pay.dto.request.DepotRequest;
import om.example.om_pay.dto.request.PaiementRequest;
import om.example.om_pay.dto.request.RetraitRequest;
import om.example.om_pay.dto.request.TransfertRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.service.ITransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Effectuer un transfert entre clients
     */
    @PostMapping("/transfert")
    public ResponseEntity<TransactionResponse> transfert(@Valid @RequestBody TransfertRequest request) {
        TransactionResponse response = transactionService.transfert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Effectuer un dépôt (distributeur -> client)
     */
    @PostMapping("/depot")
    public ResponseEntity<TransactionResponse> depot(@Valid @RequestBody DepotRequest request) {
        TransactionResponse response = transactionService.depot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Effectuer un retrait (client -> distributeur)
     */
    @PostMapping("/retrait")
    public ResponseEntity<TransactionResponse> retrait(@Valid @RequestBody RetraitRequest request) {
        TransactionResponse response = transactionService.retrait(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Effectuer un paiement marchand
     */
    @PostMapping("/paiement")
    public ResponseEntity<TransactionResponse> paiement(@Valid @RequestBody PaiementRequest request) {
        TransactionResponse response = transactionService.paiement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer l'historique des transactions d'un compte
     */
    @GetMapping("/historique/{numeroCompte}")
    public ResponseEntity<List<TransactionResponse>> getHistorique(@PathVariable String numeroCompte) {
        List<TransactionResponse> transactions = transactionService.getHistorique(numeroCompte);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Récupérer l'historique par période
     */
    @GetMapping("/historique/{numeroCompte}/periode")
    public ResponseEntity<List<TransactionResponse>> getHistoriqueByPeriode(
            @PathVariable String numeroCompte,
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {
        
        LocalDateTime debut = LocalDateTime.parse(dateDebut);
        LocalDateTime fin = LocalDateTime.parse(dateFin);
        
        List<TransactionResponse> transactions = transactionService.getHistoriqueByPeriode(numeroCompte, debut, fin);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Annuler une transaction
     */
    @PostMapping("/annuler/{reference}")
    public ResponseEntity<Void> annuler(@PathVariable String reference) {
        transactionService.annuler(reference);
        return ResponseEntity.ok().build();
    }
}
