package om.example.om_pay.exception;

/**
 * Exception pour les requêtes invalides (HTTP 400)
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
