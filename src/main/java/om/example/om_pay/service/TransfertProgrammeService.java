package om.example.om_pay.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import om.example.om_pay.dto.request.TransfertRequest;
import om.example.om_pay.dto.response.TransactionResponse;
import om.example.om_pay.exception.BadRequestException;
import om.example.om_pay.model.TransfertProgramme;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.StatutTransfertProgramme;
import om.example.om_pay.repository.TransfertProgrammeRepository;
import om.example.om_pay.repository.UtilisateurRepository;

@Service
public class TransfertProgrammeService {

    private final TransfertProgrammeRepository transfertProgrammeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ITransactionService transactionService;

    public TransfertProgrammeService(
            TransfertProgrammeRepository transfertProgrammeRepository,
            UtilisateurRepository utilisateurRepository,
            ITransactionService transactionService) {
        this.transfertProgrammeRepository = transfertProgrammeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public TransactionResponse programmerTransfert(
            String telephoneDestinataire, 
            Double montant, 
            LocalDateTime dateExecution) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utilisateur expediteur = utilisateurRepository.findByTelephone(auth.getName())
            .orElseThrow(() -> new BadRequestException("Utilisateur introuvable"));

        if (dateExecution == null) {
            throw new BadRequestException("La date d'exécution est obligatoire");
        }
        
        LocalDateTime maintenant = LocalDateTime.now();
        if (dateExecution.isBefore(maintenant)) {
            throw new BadRequestException("La date d'exécution doit être dans le futur");
        }
        
        if (dateExecution.isAfter(maintenant.plusYears(1))) {
            throw new BadRequestException("La date d'exécution ne peut pas dépasser 1 an");
        }

        TransfertProgramme transfert = new TransfertProgramme();
        transfert.setUtilisateurExpediteur(expediteur);
        transfert.setTelephoneDestinataire(telephoneDestinataire);
        transfert.setMontant(montant);
        transfert.setDateExecution(dateExecution);
        transfert.setStatut(StatutTransfertProgramme.ACTIF);
        
        TransfertProgramme saved = transfertProgrammeRepository.save(transfert);

        TransactionResponse response = new TransactionResponse();
        response.setMontant(montant);
        response.setDateCreation(LocalDateTime.now());
        
        return response;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void verifierEtExecuterTransferts() {
        LocalDateTime maintenant = LocalDateTime.now();
        
        List<TransfertProgramme> transferts = transfertProgrammeRepository
            .findByStatutAndDateExecutionBefore(StatutTransfertProgramme.ACTIF, maintenant);
        
        if (!transferts.isEmpty()) {
            System.out.println("🔄 " + transferts.size() + " transfert(s) programmé(s) à exécuter");
        }
        
        for (TransfertProgramme transfert : transferts) {
            executerTransfertProgramme(transfert);
        }
    }

    @Transactional
    public void executerTransfertProgramme(TransfertProgramme transfert) {
        System.out.println("💸 Exécution du transfert programmé #" + transfert.getId() + 
            " de " + transfert.getMontant() + " FCFA vers " + transfert.getTelephoneDestinataire());

        try {
            // Créer une authentification temporaire pour l'utilisateur expéditeur
            String telephone = transfert.getUtilisateurExpediteur().getTelephone();
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(telephone, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            TransfertRequest request = new TransfertRequest();
            request.setTelephoneDestinataire(transfert.getTelephoneDestinataire());
            request.setMontant(transfert.getMontant());

            transactionService.transfert(request);

            transfert.setStatut(StatutTransfertProgramme.TERMINE);
            transfert.setDateExecutionReelle(LocalDateTime.now());
            transfertProgrammeRepository.save(transfert);
            
            System.out.println("✅ Transfert programmé #" + transfert.getId() + " exécuté avec succès");

        } catch (Exception e) {
            transfert.setStatut(StatutTransfertProgramme.ECHOUE);
            transfert.setMessageErreur(e.getMessage());
            transfertProgrammeRepository.save(transfert);
            
            System.err.println("❌ Échec du transfert programmé #" + transfert.getId() + ": " + e.getMessage());
        } finally {
            // Nettoyer le contexte de sécurité
            SecurityContextHolder.clearContext();
        }
    }

    public List<TransfertProgramme> listerMesTransfertsProgrammes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(auth.getName())
            .orElseThrow(() -> new BadRequestException("Utilisateur introuvable"));

        return transfertProgrammeRepository
            .findByUtilisateurExpediteur_IdOrderByDateCreationDesc(utilisateur.getId());
    }

    @Transactional
    public void annulerTransfertProgramme(Long transfertId) {
        TransfertProgramme transfert = transfertProgrammeRepository.findById(transfertId)
            .orElseThrow(() -> new BadRequestException("Transfert programmé introuvable"));

        if (transfert.getStatut() != StatutTransfertProgramme.ACTIF) {
            throw new BadRequestException("Ce transfert ne peut pas être annulé (statut: " + transfert.getStatut() + ")");
        }

        transfert.setStatut(StatutTransfertProgramme.ANNULE);
        transfertProgrammeRepository.save(transfert);
    }
}
