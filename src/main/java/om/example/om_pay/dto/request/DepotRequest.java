package om.example.om_pay.dto.request;

import om.example.om_pay.validations.annotations.ValidMontant;

public class DepotRequest {

    private String telephoneClient;
    
    // NOUVEAU : Support du QR code comme alternative au téléphone
    private String codeQr;

    @ValidMontant(min = 100, max = 5000000)
    private Double montant;
    
    private String description;

    // Constructeurs
    public DepotRequest() {
    }

    public DepotRequest(String telephoneClient, Double montant) {
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
        return (telephoneClient != null && !telephoneClient.isBlank()) 
            || (codeQr != null && !codeQr.isBlank());
    }
}
