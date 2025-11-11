package om.example.om_pay.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Événement publié lorsqu'un code secret doit être envoyé par email
 */
@Getter
public class CodeSecretEnvoyeEvent extends ApplicationEvent {
    
    private final String email;
    private final String codeSecret;
    private final String nomUtilisateur;

    public CodeSecretEnvoyeEvent(Object source, String email, String codeSecret, String nomUtilisateur) {
        super(source);
        this.email = email;
        this.codeSecret = codeSecret;
        this.nomUtilisateur = nomUtilisateur;
    }
}
