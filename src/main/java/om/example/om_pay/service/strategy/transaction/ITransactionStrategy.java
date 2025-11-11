package om.example.om_pay.service.strategy.transaction;

import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.model.enums.TypeTransaction;

/**
 * Interface commune pour toutes les strategies de transaction.
 * Chaque type de transaction (TRANSFERT, DEPOT, RETRAIT, PAIEMENT) 
 * implémente cette interface avec sa logique spécifique.
 */
public interface ITransactionStrategy {
    
    /**
     * Exécute une transaction selon la stratégie spécifique
     * 
     * @param request - Objet contenant les données de la transaction
     *                  (TransfertRequest, DepotRequest, RetraitRequest, ou PaiementRequest)
     * @return TransactionResponse - Résultat de la transaction
     */
    TransactionResponse executer(Object request);
    
    /**
     * Retourne le type de transaction géré par cette stratégie
     * 
     * @return TypeTransaction (TRANSFERT, DEPOT, RETRAIT, ou PAIEMENT)
     */
    TypeTransaction getType();
}
