package om.example.om_pay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

/**
 * Configuration pour l'exécution asynchrone des événements
 * Permet notamment d'envoyer les emails en arrière-plan
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Nombre de threads minimum dans le pool
        executor.setCorePoolSize(2);
        
        // Nombre maximum de threads
        executor.setMaxPoolSize(5);
        
        // Taille de la file d'attente
        executor.setQueueCapacity(100);
        
        // Préfixe pour les noms de threads (facilite le debug)
        executor.setThreadNamePrefix("email-async-");
        
        executor.initialize();
        return executor;
    }
}
