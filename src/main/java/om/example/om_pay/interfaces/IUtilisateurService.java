package om.example.om_pay.interfaces;

import java.util.List;

import om.example.om_pay.dto.request.UpdateUtilisateurRequest;
import om.example.om_pay.dto.response.UtilisateurResponse;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Statut;

public interface IUtilisateurService {
    
  
    Utilisateur getById(Long id);
    
    Utilisateur getByTelephone(String telephone);

    Utilisateur getCurrentUser();
    
    UtilisateurResponse updateUtilisateur(Long id, UpdateUtilisateurRequest request);

    void changeStatut(Long id, Statut statut);

    void bloquer(Long id);

    void debloquer(Long id);

    List<UtilisateurResponse> getAllUtilisateurs();

    void deleteUtilisateur(Long id);

    boolean verifyCodePin(String telephone, String codePin);
    
    void changeCodePin(String telephone, String ancienPin, String nouveauPin);
}
