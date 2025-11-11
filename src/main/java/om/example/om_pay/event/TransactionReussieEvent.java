package om.example.om_pay.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import om.example.om_pay.model.Transaction;

/**
 * Événement publié lorsqu'une transaction est réussie
 */
@Getter
public class TransactionReussieEvent extends ApplicationEvent {
    
    private final Transaction transaction;
    private final String emailUtilisateur;

    public TransactionReussieEvent(Object source, Transaction transaction, String emailUtilisateur) {
        super(source);
        this.transaction = transaction;
        this.emailUtilisateur = emailUtilisateur;
    }
}
