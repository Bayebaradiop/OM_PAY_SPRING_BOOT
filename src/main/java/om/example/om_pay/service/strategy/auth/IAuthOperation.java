package om.example.om_pay.service.strategy.auth;

import om.example.om_pay.model.enums.TypeAuthOperation;

/**
 * Interface commune pour toutes les opérations d'authentification.
 * Chaque opération (register, login, verify, etc.) implémente cette interface.
 * 
 * Pattern: Strategy + Command
 */
public interface IAuthOperation<T, R> {
    
    /**
     * Exécute l'opération d'authentification
     * 
     * @param request - Objet contenant les données de la requête
     * @return R - Résultat de l'opération (AuthResponse, void, etc.)
     */
    R executer(T request);
    
    /**
     * Retourne le type de l'opération pour la Factory
     * 
     * @return TypeAuthOperation - Type de l'opération
     */
    TypeAuthOperation getType();
    
    /**
     * Retourne le nom de l'opération pour logging/debugging
     * 
     * @return String - Nom de l'opération (ex: "register", "login")
     */
    String getNomOperation();
}
