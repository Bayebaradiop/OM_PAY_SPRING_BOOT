package om.example.om_pay.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import om.example.om_pay.model.Compte;

/**
 * Événement publié lors de la création d'un nouveau compte
 */
@Getter
public class CompteCreationEvent extends ApplicationEvent {
    
    private final Compte compte;
    private final String emailUtilisateur;

    public CompteCreationEvent(Object source, Compte compte, String emailUtilisateur) {
        super(source);
        this.compte = compte;
        this.emailUtilisateur = emailUtilisateur;
    }
}
