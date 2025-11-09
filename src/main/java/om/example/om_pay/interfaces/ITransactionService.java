package om.example.om_pay.interfaces;

import java.time.LocalDateTime;
import java.util.List;

import om.example.om_pay.dto.request.DepotRequest;
import om.example.om_pay.dto.request.PaiementRequest;
import om.example.om_pay.dto.request.RetraitRequest;
import om.example.om_pay.dto.request.TransfertRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.model.Transaction;

public interface ITransactionService {
    
    TransactionResponse transfert(TransfertRequest request);
    
    TransactionResponse depot(DepotRequest request);
    
    TransactionResponse retrait(RetraitRequest request);
    
    TransactionResponse paiement(PaiementRequest request);
    
    Transaction getByReference(String reference);
    
    List<TransactionResponse> getHistorique(String numeroCompte);
    
    List<TransactionResponse> getHistoriqueByPeriode(String numeroCompte, LocalDateTime dateDebut, LocalDateTime dateFin);
    
    Double calculerFrais(Double montant, String typeTransaction);
    
    void annuler(String reference);
}
