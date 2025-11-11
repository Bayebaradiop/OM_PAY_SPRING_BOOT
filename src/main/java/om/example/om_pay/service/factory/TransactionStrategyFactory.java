package om.example.om_pay.service.factory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import om.example.om_pay.model.enums.TypeTransaction;
import om.example.om_pay.service.strategy.transaction.ITransactionStrategy;

/**
 * Factory pour sélectionner automatiquement la bonne strategy de transaction.
 * 
 * Fonctionnement :
 * - Au démarrage, Spring injecte toutes les implémentations de ITransactionStrategy
 * - Construit une Map : TypeTransaction → Strategy correspondante
 * - À l'exécution, retourne la strategy appropriée selon le type demandé
 * 
 * Pattern : Factory + Strategy
 * - Découplage : Le service appelant ne connaît pas les implémentations concrètes
 * - Extensibilité : Ajouter une nouvelle strategy = auto-détection par Spring
 */
@Component
public class TransactionStrategyFactory {
    
    /**
     * Map associant chaque type de transaction à sa strategy
     * Exemple : TRANSFERT → TransfertStrategy, DEPOT → DepotStrategy, etc.
     */
    private final Map<TypeTransaction, ITransactionStrategy> strategies;
    
    /**
     * Constructeur avec injection automatique de toutes les strategies.
     * Spring détecte automatiquement toutes les classes @Service 
     * qui implémentent ITransactionStrategy.
     * 
     * @param strategyList - Liste de toutes les strategies disponibles
     */
    public TransactionStrategyFactory(List<ITransactionStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                ITransactionStrategy::getType,  // Clé : type de transaction
                strategy -> strategy             // Valeur : instance de la strategy
            ));
    }
    
    /**
     * Retourne la strategy appropriée selon le type de transaction.
     * 
     * @param type - Type de transaction (TRANSFERT, DEPOT, RETRAIT, PAIEMENT)
     * @return ITransactionStrategy - Strategy correspondante
     * @throws IllegalArgumentException si le type n'est pas supporté
     */
    public ITransactionStrategy getStrategy(TypeTransaction type) {
        ITransactionStrategy strategy = strategies.get(type);
        
        if (strategy == null) {
            throw new IllegalArgumentException(
                "Type de transaction non supporté: " + type
            );
        }
        
        return strategy;
    }
}
