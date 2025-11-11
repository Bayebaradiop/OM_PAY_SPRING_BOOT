package om.example.om_pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import om.example.om_pay.model.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String telephone;
    private String nom;
    private String prenom;
    private Role role;

    // Constructeur personnalisé sans type (Bearer par défaut)
    public AuthResponse(String token, String telephone, String nom, String prenom, Role role) {
        this.token = token;
        this.telephone = telephone;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.type = "Bearer";
    }
}
