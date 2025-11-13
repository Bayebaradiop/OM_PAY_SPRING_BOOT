package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;

public class PaiementRequest {

    // Un seul de ces deux champs doit être fourni
    private String codeMarchand;
    private String codeQr;

    @ValidMontant(min = 100, max = 1000000)
    private Double montant;

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

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}
