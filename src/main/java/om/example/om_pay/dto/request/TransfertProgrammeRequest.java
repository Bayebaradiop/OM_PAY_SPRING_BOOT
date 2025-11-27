package om.example.om_pay.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransfertProgrammeRequest {

    @NotBlank(message = "Le numéro de téléphone du destinataire est obligatoire")
    private String telephoneDestinataire;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;

    @NotNull(message = "La date d'exécution est obligatoire")
    private LocalDateTime dateExecution;

    // Constructeurs
    public TransfertProgrammeRequest() {
    }

    public TransfertProgrammeRequest(String telephoneDestinataire, Double montant, LocalDateTime dateExecution) {
        this.telephoneDestinataire = telephoneDestinataire;
        this.montant = montant;
        this.dateExecution = dateExecution;
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

    public LocalDateTime getDateExecution() {
        return dateExecution;
    }

    public void setDateExecution(LocalDateTime dateExecution) {
        this.dateExecution = dateExecution;
    }
}
