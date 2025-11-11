package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;

public class PaiementRequest {

    private String codeMarchand;
    
    private String codeQr;
    
    private String description;

    @ValidMontant(min = 100, max = 1000000)
    private Double montant;
    
    /**
     * Validation personnalisée: au moins un des deux (codeMarchand ou codeQr) doit être fourni
     */
    public boolean isValid() {
        return (codeMarchand != null && !codeMarchand.isBlank()) || 
               (codeQr != null && !codeQr.isBlank());
    }

    // Constructeurs
    public PaiementRequest() {
    }

    public PaiementRequest(String codeMarchand, Double montant) {
        this.codeMarchand = codeMarchand;
        this.montant = montant;
    }

    // Getters et Setters
    public String getCodeMarchand() {
        return codeMarchand;
    }

    public void setCodeMarchand(String codeMarchand) {
        this.codeMarchand = codeMarchand;
    }
    
    public String getCodeQr() {
        return codeQr;
    }

    public void setCodeQr(String codeQr) {
        this.codeQr = codeQr;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}
