package om.example.om_pay.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI/Swagger pour la documentation de l'API
 * Documentation accessible via: /swagger-ui.html ou /swagger-ui/index.html
 * API Docs JSON disponible à: /v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:OM Pay API}")
    private String applicationName;

    @Value("${swagger.server.prod.url:https://om-pay-spring-boot-1.onrender.com}")
    private String prodServerUrl;

    @Value("${swagger.server.local.url:http://localhost:8083}")
    private String localServerUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        // Configuration du schéma de sécurité JWT Bearer
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                // Informations générales de l'API
                .info(new Info()
                        .title("Orange Money API - " + applicationName)
                        .version("1.0.0")
                        .description("""
                                # API REST Orange Money
                                
                                API complète pour la gestion des transactions mobiles Orange Money.
                                
                                ## 🚀 Fonctionnalités principales
                                - 💰 **Transactions** : Dépôts, retraits, transferts, paiements
                                - 👥 **Gestion utilisateurs** : Clients, distributeurs, marchands, admin
                                - 💳 **Gestion comptes** : Consultation solde, historique, blocage/déblocage
                                - 🔐 **Authentification JWT** : Tokens sécurisés avec cookies HTTP-only
                                
                                ## 🔑 Authentification
                                1. Utilisez l'endpoint `/api/auth/login` pour vous connecter
                                2. Copiez le token JWT depuis la réponse
                                3. Cliquez sur le bouton **"Authorize"** 🔓 en haut
                                4. Collez le token (sans "Bearer ") dans le champ
                                5. Cliquez sur **"Authorize"** puis **"Close"**
                                6. Testez les endpoints protégés ✅
                                
                                ## 📊 Données de test
                                
                                ### Clients (sans code PIN)
                                | Nom | Téléphone | Mot de passe | Numéro de compte |
                                |-----|-----------|--------------|------------------|
                                | Moussa Diop | 771234567 | Password123! | OM8000380279 |
                                | Fatou Sarr | 779876543 | Pass123! | OM2665616523 |
                                | Cheikh Fall | 776543210 | Client123! | OM5432147504 |
                                
                                ### Distributeur
                                | Nom | Téléphone | Mot de passe | Numéro de compte |
                                |-----|-----------|--------------|------------------|
                                | Abdou Ndiaye | 775551234 | Distrib123! | OM4274060223 |
                                
                                ## 💡 Notes importantes
                                - Les tokens JWT expirent après 24 heures
                                - Les cookies HTTP-only sont automatiquement gérés
                                - Utilisez le profil `dev` pour les tests avec données de démo
                                """)
                        .contact(new Contact()
                                .name("Orange Money Support")
                                .email("support@orangemoney.sn")
                                .url("https://www.orangemoney.sn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                
                // Configuration des serveurs
                .servers(List.of(
                        new Server()
                                .url(prodServerUrl)
                                .description("🌐 Serveur de production (Render)"),
                        new Server()
                                .url(localServerUrl)
                                .description("💻 Serveur de développement local")
                ))
                
                // Configuration de la sécurité JWT
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        ### 🔐 Authentification JWT
                                        
                                        Entrez votre token JWT obtenu via `/api/auth/login`
                                        
                                        **Format**: Token sans le préfixe "Bearer "
                                        
                                        **Exemple**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
                                        """)));
    }
}
