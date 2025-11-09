package om.example.om_pay.dto.request;

import jakarta.validation.constraints.NotBlank;
import om.example.om_pay.validations.annotations.ValidMontant;

public class PaiementRequest {

    @NotBlank(message = "{error.validation.codeMarchand.required}")
    private String codeMarchand;

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

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}
