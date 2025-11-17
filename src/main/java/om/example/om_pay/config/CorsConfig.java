package om.example.om_pay.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration CORS pour autoriser les requêtes cross-origin
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées (Frontend + Applications mobiles/console)
        // En production sur Render, autorisez toutes les origines ou spécifiez celles nécessaires
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Permet toutes les origines
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers autorisés (important pour JWT)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Headers exposés (pour que le client puisse les lire)
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type",
            "X-Total-Count"
        ));
        
        // Autoriser l'envoi des credentials (cookies, headers d'auth)
        configuration.setAllowCredentials(true);
        
        // Cache de la configuration CORS (en secondes)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
