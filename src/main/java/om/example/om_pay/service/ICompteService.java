package om.example.om_pay.service;

import java.util.List;

import om.example.om_pay.dto.response.CompteResponse;
import om.example.om_pay.model.Compte;
import om.example.om_pay.model.enums.TypeCompte;

public interface ICompteService {
    

    Compte creerCompte(Long utilisateurId, TypeCompte typeCompte);

    Compte getByNumeroCompte(String numeroCompte);
    
    Compte getById(Long id);

    List<CompteResponse> getComptesByUtilisateur(Long utilisateurId);
    
    Compte getComptePrincipal(Long utilisateurId);

    Double consulterSolde(String numeroCompte);
    
    void crediter(String numeroCompte, Double montant);
    
    void debiter(String numeroCompte, Double montant);
    
    void bloquer(String numeroCompte);
    
    void debloquer(String numeroCompte);
}
