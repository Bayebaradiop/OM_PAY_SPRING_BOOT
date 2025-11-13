package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;

public class TransfertRequest {

    // Un seul de ces deux champs doit être fourni
    private String telephoneDestinataire;
    private String codeQrDestinataire;

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

    public String getCodeQrDestinataire() {
        return codeQrDestinataire;
    }

    public void setCodeQrDestinataire(String codeQrDestinataire) {
        this.codeQrDestinataire = codeQrDestinataire;
    }
}
