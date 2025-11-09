package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;
import om.example.om_pay.validations.annotations.ValidTelephone;

public class TransfertRequest {

    @ValidTelephone
    private String telephoneDestinataire;

    @ValidMontant(min = 100, max = 1000000)
    private Double montant;

    // Constructeurs
    public TransfertRequest() {
    }

    public TransfertRequest(String telephoneDestinataire, Double montant) {
        this.telephoneDestinataire = telephoneDestinataire;
        this.montant = montant;
    }

    // Getters et Setters
    public String getTelephoneDestinataire() {
        return telephoneDestinataire;
    }

    public void setTelephoneDestinataire(String telephoneDestinataire) {
        this.telephoneDestinataire = telephoneDestinataire;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}
