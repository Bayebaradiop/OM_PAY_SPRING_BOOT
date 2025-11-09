package om.example.om_pay.interfaces;

import java.util.List;

import om.example.om_pay.model.GrilleTarification;
import om.example.om_pay.model.enums.TypeTransaction;

public interface IGrilleTarificationService {
  
    GrilleTarification creer(GrilleTarification grille);
    
    Double calculerFrais(Double montant, TypeTransaction typeTransaction);

    List<GrilleTarification> getAll();

    List<GrilleTarification> getByTypeTransaction(TypeTransaction typeTransaction);
    
    GrilleTarification update(Long id, GrilleTarification grille);
    
    void delete(Long id);
}
