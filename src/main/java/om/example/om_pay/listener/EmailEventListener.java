package om.example.om_pay.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import om.example.om_pay.event.CodeSecretEnvoyeEvent;
import om.example.om_pay.event.CompteCreationEvent;
import om.example.om_pay.event.TransactionReussieEvent;
import om.example.om_pay.service.EmailService;

/**
 * Listener qui écoute les événements et envoie des emails de manière asynchrone
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;

    /**
     * Écoute l'événement d'envoi de code secret
     * Exécuté de manière asynchrone pour ne pas bloquer le thread principal
     */
    @Async
    @EventListener
    public void handleCodeSecretEnvoye(CodeSecretEnvoyeEvent event) {
        log.info("📧 Événement reçu : Envoi code secret à {}", event.getEmail());
        
        try {
            // Appel correct : envoyerCodeSecret(email, nom, codeSecret)
            emailService.envoyerCodeSecret(
                event.getEmail(),
                event.getNomUtilisateur(),
                event.getCodeSecret()  // Le code secret en 3ème paramètre
            );
            log.info("✅ Email code secret envoyé avec succès à {}", event.getEmail());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi du code secret à {}: {}", 
                event.getEmail(), e.getMessage());
        }
    }

    /**
     * Écoute l'événement de création de compte
     */
    @Async
    @EventListener
    public void handleCompteCreation(CompteCreationEvent event) {
        log.info("📧 Événement reçu : Création compte {}", event.getCompte().getNumeroCompte());
        
        try {
            String nomUtilisateur = event.getCompte().getUtilisateur().getNom() + " " + 
                                   event.getCompte().getUtilisateur().getPrenom();
            
            // Envoyer un email de bienvenue avec le numéro de compte
            String message = String.format(
                "Félicitations ! Votre compte OM_PAY a été créé avec succès.\n\n" +
                "Numéro de compte : %s\n\n" +
                "Vous pouvez maintenant effectuer des transactions.",
                event.getCompte().getNumeroCompte()
            );
            
            emailService.envoyerCodeSecret(
                event.getEmailUtilisateur(),
                nomUtilisateur,
                message
            );
            
            log.info("✅ Email confirmation compte envoyé à {}", event.getEmailUtilisateur());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi email création compte: {}", e.getMessage());
        }
    }

    /**
     * Écoute l'événement de transaction réussie
     */
    @Async
    @EventListener
    public void handleTransactionReussie(TransactionReussieEvent event) {
        log.info("📧 Événement reçu : Transaction {} réussie", 
            event.getTransaction().getReference());
        
        try {
            String message = String.format(
                "Transaction réussie !\n\n" +
                "Référence : %s\n" +
                "Type : %s\n" +
                "Montant : %.2f FCFA\n" +
                "Frais : %.2f FCFA\n" +
                "Date : %s",
                event.getTransaction().getReference(),
                event.getTransaction().getTypeTransaction(),
                event.getTransaction().getMontant(),
                event.getTransaction().getFrais(),
                event.getTransaction().getDateTransaction()
            );
            
            emailService.envoyerCodeSecret(
                event.getEmailUtilisateur(),
                "Transaction OM_PAY",
                message
            );
            
            log.info("✅ Email confirmation transaction envoyé à {}", event.getEmailUtilisateur());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi email transaction: {}", e.getMessage());
        }
    }
}
