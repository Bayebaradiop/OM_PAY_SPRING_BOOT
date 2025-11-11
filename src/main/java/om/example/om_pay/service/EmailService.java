package om.example.om_pay.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service d'envoi d'emails via l'API Brevo (ex-Sendinblue)
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api.key}")
    private String brevoApiKey;
    
    @Value("${brevo.api.url}")
    private String brevoApiUrl;
    
    @Value("${mail.from.address}")
    private String fromAddress;
    
    @Value("${mail.from.name}")
    private String fromName;
    
    private final RestTemplate restTemplate;
    
    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Envoie un email avec le code secret pour l'activation du compte
     * @param destinataire Email du destinataire
     * @param nom Nom complet du destinataire
     * @param codeSecret Code secret à envoyer
     */
    public void envoyerCodeSecret(String destinataire, String nom, String codeSecret) {
        String htmlContent = construireEmailCodeSecret(nom, codeSecret);
        
        // Construction de la requête Brevo
        Map<String, Object> emailData = Map.of(
            "sender", Map.of(
                "email", fromAddress,
                "name", fromName
            ),
            "to", List.of(Map.of("email", destinataire, "name", nom)),
            "subject", "🔐 Code secret - Activation de votre compte Faysany Banque",
            "htmlContent", htmlContent
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("api-key", brevoApiKey);
        headers.set("content-type", "application/json");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailData, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                brevoApiUrl, 
                request, 
                String.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Erreur lors de l'envoi de l'email. Status: {}", response.getStatusCode());
                throw new RuntimeException("Erreur lors de l'envoi de l'email");
            }
            
            log.info("Email envoyé avec succès à {}", destinataire);
            
        } catch (Exception e) {
            log.error("Impossible d'envoyer l'email à {}: {}", destinataire, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email: " + e.getMessage());
        }
    }
    
    /**
     * Construit le template HTML de l'email avec le code secret
     */
    private String construireEmailCodeSecret(String nom, String codeSecret) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                             color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .code-box { background: white; border: 2px dashed #667eea; 
                               padding: 25px; margin: 25px 0; text-align: center; border-radius: 8px;
                               box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .code { font-size: 36px; font-weight: bold; color: #667eea; letter-spacing: 8px;
                           font-family: 'Courier New', monospace; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; padding: 20px; }
                    .warning { background: #fff3cd; border-left: 4px solid #ffc107; 
                              padding: 15px; margin: 20px 0; border-radius: 4px; }
                    .warning strong { color: #856404; }
                    .warning ul { margin: 10px 0; padding-left: 20px; }
                    .warning li { margin: 5px 0; }
                    .btn { display: inline-block; padding: 12px 30px; background: #667eea;
                          color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Bienvenue chez Faysany Banque</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>%s</strong>,</p>
                        
                        <p>Merci pour votre inscription ! Votre compte a été créé avec succès.</p>
                        
                        <p>Pour activer votre compte, veuillez utiliser le code secret ci-dessous 
                           lors de votre première connexion :</p>
                        
                        <div class="code-box">
                            <div class="code">%s</div>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Important :</strong>
                            <ul>
                                <li>Ce code est valide pendant <strong>30 minutes</strong></li>
                                <li>Il est nécessaire uniquement pour votre <strong>première connexion</strong></li>
                                <li>Ne partagez jamais ce code avec qui que ce soit</li>
                                <li>Notre équipe ne vous demandera jamais ce code par téléphone ou email</li>
                            </ul>
                        </div>
                        
                        <p><strong>Comment utiliser ce code ?</strong></p>
                        <ol>
                            <li>Rendez-vous sur votre espace client</li>
                            <li>Entrez votre numéro de téléphone</li>
                            <li>Saisissez le code secret ci-dessus</li>
                            <li>Créez votre mot de passe</li>
                        </ol>
                        
                        <p>Si vous n'êtes pas à l'origine de cette inscription, 
                           veuillez ignorer cet email ou nous contacter immédiatement.</p>
                        
                        <p>Cordialement,<br>
                        <strong>L'équipe Faysany Banque</strong></p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Faysany Banque. Tous droits réservés.</p>
                        <p style="color: #999; font-size: 11px; margin-top: 10px;">
                            Cet email a été envoyé automatiquement, merci de ne pas y répondre.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nom, codeSecret);
    }
}
