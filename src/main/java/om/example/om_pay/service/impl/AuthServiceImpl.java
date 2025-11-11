package om.example.om_pay.service.impl;

import org.springframework.stereotype.Service;

import om.example.om_pay.dto.request.ChangePasswordRequest;
import om.example.om_pay.dto.request.LoginRequest;
import om.example.om_pay.dto.request.RegisterRequest;
import om.example.om_pay.dto.request.VerifyCodeSecretRequest;
import om.example.om_pay.dto.response.AuthResponse;
import om.example.om_pay.dto.response.ProfilCompletResponse;
import om.example.om_pay.model.enums.TypeAuthOperation;
import om.example.om_pay.repository.UtilisateurRepository;
import om.example.om_pay.service.IAuthService;
import om.example.om_pay.service.ProfilService;
import om.example.om_pay.service.factory.AuthOperationFactory;
import om.example.om_pay.service.strategy.auth.IAuthOperation;

/**
 * Implémentation du service d'authentification.
 * 
 * Architecture : Strategy Pattern avec Factory
 * - Utilise AuthOperationFactory pour sélectionner dynamiquement l'opération
 * - Chaque opération (register, login, etc.) est déléguée à une classe spécialisée
 * - Respect des principes SOLID (Single Responsibility, Open/Closed)
 */
@Service
public class AuthServiceImpl implements IAuthService {

    private final AuthOperationFactory operationFactory;
    private final UtilisateurRepository utilisateurRepository;
    private final ProfilService profilService;

    public AuthServiceImpl(
            AuthOperationFactory operationFactory,
            UtilisateurRepository utilisateurRepository,
            ProfilService profilService) {
        this.operationFactory = operationFactory;
        this.utilisateurRepository = utilisateurRepository;
        this.profilService = profilService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        return this.<RegisterRequest, AuthResponse>getOperation(TypeAuthOperation.REGISTER)
                .executer(request);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return this.<LoginRequest, AuthResponse>getOperation(TypeAuthOperation.LOGIN)
                .executer(request);
    }

    @Override
    public AuthResponse verifierCodeSecret(VerifyCodeSecretRequest request) {
        return this.<VerifyCodeSecretRequest, AuthResponse>getOperation(TypeAuthOperation.VERIFY_CODE_SECRET)
                .executer(request);
    }

    @Override
    public void renvoyerCodeSecret(String telephone) {
        this.<String, Void>getOperation(TypeAuthOperation.RESEND_CODE_SECRET)
                .executer(telephone);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        this.<ChangePasswordRequest, Void>getOperation(TypeAuthOperation.CHANGE_PASSWORD)
                .executer(request);
    }

    @Override
    public void logout(String token) {
        // Implémenter la logique de logout si nécessaire
        // (ex: blacklist de tokens)
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return this.<String, AuthResponse>getOperation(TypeAuthOperation.REFRESH_TOKEN)
                .executer(refreshToken);
    }

    @Override
    public boolean telephoneExists(String telephone) {
        return utilisateurRepository.existsByTelephone(telephone);
    }

    @Override
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
    
    @Override
    public ProfilCompletResponse getProfilComplet() {
        return profilService.getProfilComplet();
    }
    
    private <T, R> IAuthOperation<T, R> getOperation(TypeAuthOperation type) {
        return operationFactory.getOperation(type);
    }
}
