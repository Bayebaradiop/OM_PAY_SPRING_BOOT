package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;
import om.example.om_pay.validations.annotations.ValidTelephone;

public class RetraitRequest {

    @ValidTelephone
    private String telephoneClient;

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
}
