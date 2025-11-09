package om.example.om_pay.interfaces;

import om.example.om_pay.dto.request.ChangePasswordRequest;
import om.example.om_pay.dto.request.LoginRequest;
import om.example.om_pay.dto.request.RegisterRequest;
import om.example.om_pay.dto.response.AuthResponse;

public interface IAuthService {
    

    AuthResponse register(RegisterRequest request);
 
    AuthResponse login(LoginRequest request);
    
    void changePassword(ChangePasswordRequest request);
    
    void logout(String token);
    
    AuthResponse refreshToken(String refreshToken);
    
    boolean telephoneExists(String telephone);

    boolean emailExists(String email);
}
