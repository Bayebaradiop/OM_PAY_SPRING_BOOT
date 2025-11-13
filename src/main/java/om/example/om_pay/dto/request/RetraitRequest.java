package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;

public class RetraitRequest {

    // Un seul de ces deux champs doit être fourni
    private String telephoneClient;
    private String codeQr;

    @ValidMontant(min = 100, max = 500000)
    private Double montant;

    // Constructeurs
    public RetraitRequest() {
    }

    public RetraitRequest(String telephoneClient, Double montant) {
        this.telephoneClient = telephoneClient;
        this.montant = montant;
    }

    // Getters et Setters
    public String getTelephoneClient() {
        return telephoneClient;
    }

    public void setTelephoneClient(String telephoneClient) {
        this.telephoneClient = telephoneClient;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getCodeQr() {
        return codeQr;
    }

    public void setCodeQr(String codeQr) {
        this.codeQr = codeQr;
    }
}
