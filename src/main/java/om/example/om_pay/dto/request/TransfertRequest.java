package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;

public class TransfertRequest {

    private String telephoneDestinataire;
    
    // NOUVEAU : Support du QR code comme alternative au téléphone
    private String codeQrDestinataire;

    @ValidMontant(min = 100, max = 1000000)
    private Double montant;
    
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Validation : au moins un des deux doit être fourni (téléphone OU QR code)
     */
    public boolean isValid() {
        return (telephoneDestinataire != null && !telephoneDestinataire.isBlank()) 
            || (codeQrDestinataire != null && !codeQrDestinataire.isBlank());
    }
}
