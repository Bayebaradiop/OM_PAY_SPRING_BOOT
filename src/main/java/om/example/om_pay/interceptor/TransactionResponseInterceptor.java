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
import om.example.om_pay.controller.TransactionController;

@RestControllerAdvice(assignableTypes = TransactionController.class)
public class TransactionResponseInterceptor implements ResponseBodyAdvice<Object> {

    private static final Map<String, String> METHOD_MESSAGES = Map.ofEntries(
        Map.entry("transfert", "Transfert effectué avec succès"),
        Map.entry("depot", "Dépôt effectué avec succès"),
        Map.entry("retrait", "Retrait effectué avec succès"),
        Map.entry("paiement", "Paiement effectué avec succès"),
        Map.entry("getHistorique", "Historique récupéré avec succès"),
        Map.entry("getHistoriqueByPeriode", "Historique récupéré avec succès"),
        Map.entry("annuler", "Transaction annulée avec succès")
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
        String message = METHOD_MESSAGES.getOrDefault(methodName, "Opération réussie");
        
        return ApiResponse.success(message, body);
    }
}
