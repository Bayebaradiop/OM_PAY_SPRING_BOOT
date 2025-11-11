package om.example.om_pay.service.factory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import om.example.om_pay.model.enums.TypeAuthOperation;
import om.example.om_pay.service.strategy.auth.IAuthOperation;

/**
 * Factory pour sélectionner automatiquement la bonne opération d'authentification.
 * 
 * Fonctionnement :
 * - Au démarrage, Spring injecte toutes les implémentations de IAuthOperation
 * - Construit une Map : TypeAuthOperation → Operation correspondante
 * - À l'exécution, retourne l'opération appropriée selon le type demandé
 * 
 * Pattern : Factory + Strategy
 * 
 * Avantages :
 * - Ajout de nouvelles operations sans modifier la factory
 * - Sélection dynamique basée sur le type
 * - Respect du principe Open/Closed (SOLID)
 */
@Component
public class AuthOperationFactory {
    
    private final Map<TypeAuthOperation, IAuthOperation<?, ?>> operations;
    
    /**
     * Constructeur avec injection de toutes les operations auth.
     * Spring injecte automatiquement toutes les classes annotées @Service
     * qui implémentent IAuthOperation.
     * 
     * @param operationList - Liste de toutes les operations injectées par Spring
     */
    public AuthOperationFactory(List<IAuthOperation<?, ?>> operationList) {
        // Créer une Map : TypeAuthOperation → Operation
        this.operations = operationList.stream()
                .collect(Collectors.toMap(
                        IAuthOperation::getType,
                        operation -> operation
                ));
    }
    
    /**
     * Retourne l'opération correspondant au type demandé.
     * 
     * @param type - Type d'opération (REGISTER, LOGIN, etc.)
     * @return IAuthOperation - L'opération correspondante
     * @throws IllegalArgumentException si le type n'est pas supporté
     */
    @SuppressWarnings("unchecked")
    public <T, R> IAuthOperation<T, R> getOperation(TypeAuthOperation type) {
        IAuthOperation<?, ?> operation = operations.get(type);
        
        if (operation == null) {
            throw new IllegalArgumentException(
                    "Aucune opération trouvée pour le type: " + type);
        }
        
        return (IAuthOperation<T, R>) operation;
    }
}
