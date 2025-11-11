package om.example.om_pay.interceptor;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import om.example.om_pay.config.ApiResponse;
import om.example.om_pay.controller.AuthController;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthResponseInterceptor implements ResponseBodyAdvice<Object> {

    private static final Map<String, String> METHOD_MESSAGES = Map.ofEntries(
        Map.entry("register", "Inscription réussie ! Vérifiez votre email pour le code secret."),
        Map.entry("verifyCodeSecret", "Compte activé avec succès. Bienvenue !"),
        Map.entry("resendCodeSecret", "Un nouveau code secret a été envoyé à votre email."),
        Map.entry("login", "Connexion réussie"),
        Map.entry("changePassword", "Mot de passe modifié avec succès"),
        Map.entry("getCurrentUser", "Informations utilisateur récupérées"),
        Map.entry("refreshToken", "Token rafraîchi avec succès"),
        Map.entry("logout", "Déconnexion réussie. Cookie supprimé.")
    );

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                 ServerHttpRequest request, ServerHttpResponse response) {
        
        if (body instanceof ApiResponse) {
            return body;
        }
        
        String methodName = returnType.getMethod().getName();
        
        // Récupérer le message depuis la Map, ou utiliser un message par défaut
        String message = METHOD_MESSAGES.getOrDefault(methodName, "Opération réussie");
        
        return ApiResponse.success(message, body);
    }
}
