package om.example.om_pay.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import om.example.om_pay.model.Compte;
import om.example.om_pay.model.Marchand;
import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.model.enums.Role;
import om.example.om_pay.model.enums.Statut;
import om.example.om_pay.model.enums.TypeCompte;
import om.example.om_pay.repository.CompteRepository;
import om.example.om_pay.repository.MarchandRepository;
import om.example.om_pay.repository.UtilisateurRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final CompteRepository compteRepository;
    private final MarchandRepository marchandRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
        UtilisateurRepository utilisateurRepository,
        CompteRepository compteRepository,
        MarchandRepository marchandRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.compteRepository = compteRepository;
        this.marchandRepository = marchandRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si les données existent déjà
        if (utilisateurRepository.count() > 0) {
            System.out.println("✓ Les données existent déjà. Seeding ignoré.");
            return;
        }

        System.out.println("\n=== DÉMARRAGE DU SEEDING ===\n");

        // 1. Créer des clients
        seedClients();

        // 2. Créer des distributeurs
        seedDistributeurs();

        // 3. Créer des marchands
        seedMarchands();

        // 4. Créer un admin
        seedAdmin();

        System.out.println("\n=== SEEDING TERMINÉ ===\n");
    }

    private void seedClients() {
        System.out.println("→ Création des clients...");

        // Client 1
        Utilisateur client1 = new Utilisateur();
        client1.setNom("Diop");
        client1.setPrenom("Moussa");
        client1.setTelephone("771234567");
        client1.setEmail("moussa@test.com");
        client1.setMotDePasse(passwordEncoder.encode("Password123!"));
        client1.setCodePin(passwordEncoder.encode("123456"));
        client1.setRole(Role.CLIENT);
        client1.setStatut(Statut.ACTIF);
        client1.setPremiereConnexion(false); // Compte déjà activé
        client1.setDateCreation(LocalDateTime.now());
        client1 = utilisateurRepository.save(client1);

        // Créer compte pour client 1
        Compte compteClient1 = new Compte();
        compteClient1.setNumeroCompte("OM8000380279"); // Numéro fixe pour Swagger
        compteClient1.setSolde(50000.0);
        compteClient1.setTypeCompte(TypeCompte.PRINCIPAL);
        compteClient1.setStatut(Statut.ACTIF);
        compteClient1.setUtilisateur(client1);
        compteClient1.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteClient1);

        System.out.println("  ✓ Client: Moussa Diop (771234567) - Compte: " + compteClient1.getNumeroCompte() + " - Solde: 50,000 FCFA");

        // Client 2
        Utilisateur client2 = new Utilisateur();
        client2.setNom("Sarr");
        client2.setPrenom("Fatou");
        client2.setTelephone("779876543");
        client2.setEmail("fatou@test.com");
        client2.setMotDePasse(passwordEncoder.encode("Pass123!"));
        client2.setCodePin(passwordEncoder.encode("123456"));
        client2.setRole(Role.CLIENT);
        client2.setStatut(Statut.ACTIF);
        client2.setPremiereConnexion(false);
        client2.setDateCreation(LocalDateTime.now());
        client2 = utilisateurRepository.save(client2);

        // Créer compte pour client 2
        Compte compteClient2 = new Compte();
        compteClient2.setNumeroCompte("OM2665616523"); // Numéro fixe pour Swagger
        compteClient2.setSolde(25000.0);
        compteClient2.setTypeCompte(TypeCompte.PRINCIPAL);
        compteClient2.setStatut(Statut.ACTIF);
        compteClient2.setUtilisateur(client2);
        compteClient2.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteClient2);

        System.out.println("  ✓ Client: Fatou Sarr (779876543) - Compte: " + compteClient2.getNumeroCompte() + " - Solde: 25,000 FCFA");

        // Client 3
        Utilisateur client3 = new Utilisateur();
        client3.setNom("Fall");
        client3.setPrenom("Cheikh");
        client3.setTelephone("776543210");
        client3.setEmail("cheikh@test.com");
        client3.setMotDePasse(passwordEncoder.encode("Client123!"));
        client3.setCodePin(passwordEncoder.encode("123456"));
        client3.setRole(Role.CLIENT);
        client3.setStatut(Statut.ACTIF);
        client3.setPremiereConnexion(false);
        client3.setDateCreation(LocalDateTime.now());
        client3 = utilisateurRepository.save(client3);

        // Créer compte pour client 3
        Compte compteClient3 = new Compte();
        compteClient3.setNumeroCompte("OM5432147504"); // Numéro fixe pour Swagger
        compteClient3.setSolde(100000.0);
        compteClient3.setTypeCompte(TypeCompte.PRINCIPAL);
        compteClient3.setStatut(Statut.ACTIF);
        compteClient3.setUtilisateur(client3);
        compteClient3.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteClient3);

        System.out.println("  ✓ Client: Cheikh Fall (776543210) - Compte: " + compteClient3.getNumeroCompte() + " - Solde: 100,000 FCFA");
    }

    private void seedDistributeurs() {
        System.out.println("\n→ Création des distributeurs...");

        // Distributeur 1
        Utilisateur distributeur1 = new Utilisateur();
        distributeur1.setNom("Ndiaye");
        distributeur1.setPrenom("Abdou");
        distributeur1.setTelephone("775551234");
        distributeur1.setEmail("abdou@test.com");
        distributeur1.setMotDePasse(passwordEncoder.encode("Distrib123!"));
        distributeur1.setCodePin(passwordEncoder.encode("123456"));
        distributeur1.setRole(Role.DISTRIBUTEUR);
        distributeur1.setStatut(Statut.ACTIF);
        distributeur1.setPremiereConnexion(false);
        distributeur1.setDateCreation(LocalDateTime.now());
        distributeur1 = utilisateurRepository.save(distributeur1);

        // Créer compte pour distributeur 1
        Compte compteDistributeur1 = new Compte();
        compteDistributeur1.setNumeroCompte("OM4274060223"); // Numéro fixe pour Swagger
        compteDistributeur1.setSolde(500000.0); // 500k FCFA
        compteDistributeur1.setTypeCompte(TypeCompte.PRINCIPAL);
        compteDistributeur1.setStatut(Statut.ACTIF);
        compteDistributeur1.setUtilisateur(distributeur1);
        compteDistributeur1.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteDistributeur1);

        System.out.println("  ✓ Distributeur: Abdou Ndiaye (775551234) - Compte: " + compteDistributeur1.getNumeroCompte() + " - Solde: 500,000 FCFA");

        // Distributeur 2
        Utilisateur distributeur2 = new Utilisateur();
        distributeur2.setNom("Kane");
        distributeur2.setPrenom("Mariama");
        distributeur2.setTelephone("778889999");
        distributeur2.setEmail("mariama@distrib.com");
        distributeur2.setMotDePasse(passwordEncoder.encode("Distrib456!"));
        distributeur2.setCodePin(passwordEncoder.encode("123456"));
        distributeur2.setRole(Role.DISTRIBUTEUR);
        distributeur2.setStatut(Statut.ACTIF);
        distributeur2.setPremiereConnexion(false);
        distributeur2.setDateCreation(LocalDateTime.now());
        distributeur2 = utilisateurRepository.save(distributeur2);

        // Créer compte pour distributeur 2
        Compte compteDistributeur2 = new Compte();
        compteDistributeur2.setNumeroCompte("OM" + System.currentTimeMillis() % 10000000000L); // Numéro aléatoire pour distributeur 2
        compteDistributeur2.setSolde(750000.0); // 750k FCFA
        compteDistributeur2.setTypeCompte(TypeCompte.PRINCIPAL);
        compteDistributeur2.setStatut(Statut.ACTIF);
        compteDistributeur2.setUtilisateur(distributeur2);
        compteDistributeur2.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteDistributeur2);

        System.out.println("  ✓ Distributeur: Mariama Kane (778889999) - Compte: " + compteDistributeur2.getNumeroCompte() + " - Solde: 750,000 FCFA");
    }

    private void seedMarchands() {
        System.out.println("\n→ Création des marchands...");

        // Marchand 1 - Boutique Chez Amadou
        // D'abord créer l'utilisateur et le compte
        Utilisateur userMarchand1 = new Utilisateur();
        userMarchand1.setNom("Diallo");
        userMarchand1.setPrenom("Amadou");
        userMarchand1.setTelephone("771112233");
        userMarchand1.setEmail("amadou@shop.sn");
        userMarchand1.setMotDePasse(passwordEncoder.encode("Marchand123!"));
        userMarchand1.setCodePin(passwordEncoder.encode("123456"));
        userMarchand1.setRole(Role.MARCHAND);
        userMarchand1.setStatut(Statut.ACTIF);
        userMarchand1.setPremiereConnexion(false);
        userMarchand1.setDateCreation(LocalDateTime.now());
        userMarchand1 = utilisateurRepository.save(userMarchand1);

        Compte compteMarchand1 = new Compte();
        compteMarchand1.setNumeroCompte("OM" + System.currentTimeMillis() % 10000000000L);
        compteMarchand1.setSolde(0.0); // Les marchands commencent à 0
        compteMarchand1.setTypeCompte(TypeCompte.PRINCIPAL);
        compteMarchand1.setStatut(Statut.ACTIF);
        compteMarchand1.setUtilisateur(userMarchand1);
        compteMarchand1.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteMarchand1);

        Marchand marchand1 = new Marchand();
        marchand1.setNomCommercial("Boutique Chez Amadou");
        marchand1.setNumeroMarchand("771112233");
        marchand1.setCodeMarchand("SHOP001");
        marchand1.setCategorie("Commerce");
        marchand1.setAdresse("Dakar, Plateau");
        marchand1.setEmail("amadou@shop.sn");
        marchand1.setStatut(Statut.ACTIF);
        marchand1.setCommission(1.5);
        marchand1.setDateCreation(LocalDateTime.now());
        marchandRepository.save(marchand1);

        System.out.println("  ✓ Marchand: Boutique Chez Amadou (SHOP001) - Commission: 1.5% - QR Code auto-généré");

        // Marchand 2 - Pharmacie Yaye Fatou
        Utilisateur userMarchand2 = new Utilisateur();
        userMarchand2.setNom("Sow");
        userMarchand2.setPrenom("Fatou");
        userMarchand2.setTelephone("775556789");
        userMarchand2.setEmail("contact@pharmacie-fatou.sn");
        userMarchand2.setMotDePasse(passwordEncoder.encode("Marchand456!"));
        userMarchand2.setCodePin(passwordEncoder.encode("123456"));
        userMarchand2.setRole(Role.MARCHAND);
        userMarchand2.setStatut(Statut.ACTIF);
        userMarchand2.setPremiereConnexion(false);
        userMarchand2.setDateCreation(LocalDateTime.now());
        userMarchand2 = utilisateurRepository.save(userMarchand2);

        Compte compteMarchand2 = new Compte();
        compteMarchand2.setNumeroCompte("OM" + (System.currentTimeMillis() + 1) % 10000000000L);
        compteMarchand2.setSolde(0.0);
        compteMarchand2.setTypeCompte(TypeCompte.PRINCIPAL);
        compteMarchand2.setStatut(Statut.ACTIF);
        compteMarchand2.setUtilisateur(userMarchand2);
        compteMarchand2.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteMarchand2);

        Marchand marchand2 = new Marchand();
        marchand2.setNomCommercial("Pharmacie Yaye Fatou");
        marchand2.setNumeroMarchand("775556789");
        marchand2.setCodeMarchand("PHARM001");
        marchand2.setCategorie("Santé");
        marchand2.setAdresse("Dakar, Médina");
        marchand2.setEmail("contact@pharmacie-fatou.sn");
        marchand2.setStatut(Statut.ACTIF);
        marchand2.setCommission(2.0);
        marchand2.setDateCreation(LocalDateTime.now());
        marchandRepository.save(marchand2);

        System.out.println("  ✓ Marchand: Pharmacie Yaye Fatou (PHARM001) - Commission: 2.0% - QR Code auto-généré");

        // Marchand 3 - Supermarché Al Baraka
        Utilisateur userMarchand3 = new Utilisateur();
        userMarchand3.setNom("Ba");
        userMarchand3.setPrenom("Alioune");
        userMarchand3.setTelephone("770001122");
        userMarchand3.setEmail("info@albaraka.sn");
        userMarchand3.setMotDePasse(passwordEncoder.encode("Marchand789!"));
        userMarchand3.setCodePin(passwordEncoder.encode("123456"));
        userMarchand3.setRole(Role.MARCHAND);
        userMarchand3.setStatut(Statut.ACTIF);
        userMarchand3.setPremiereConnexion(false);
        userMarchand3.setDateCreation(LocalDateTime.now());
        userMarchand3 = utilisateurRepository.save(userMarchand3);

        Compte compteMarchand3 = new Compte();
        compteMarchand3.setNumeroCompte("OM" + (System.currentTimeMillis() + 2) % 10000000000L);
        compteMarchand3.setSolde(0.0);
        compteMarchand3.setTypeCompte(TypeCompte.PRINCIPAL);
        compteMarchand3.setStatut(Statut.ACTIF);
        compteMarchand3.setUtilisateur(userMarchand3);
        compteMarchand3.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteMarchand3);

        Marchand marchand3 = new Marchand();
        marchand3.setNomCommercial("Supermarché Al Baraka");
        marchand3.setNumeroMarchand("770001122");
        marchand3.setCodeMarchand("SUPER001");
        marchand3.setCategorie("Grande Distribution");
        marchand3.setAdresse("Dakar, Liberté 6");
        marchand3.setEmail("info@albaraka.sn");
        marchand3.setStatut(Statut.ACTIF);
        marchand3.setCommission(1.0);
        marchand3.setDateCreation(LocalDateTime.now());
        marchandRepository.save(marchand3);

        System.out.println("  ✓ Marchand: Supermarché Al Baraka (SUPER001) - Commission: 1.0% - QR Code auto-généré");
    }

    private void seedAdmin() {
        System.out.println("\n→ Création de l'administrateur...");

        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setPrenom("System");
        admin.setTelephone("771111111");
        admin.setEmail("admin@ompay.sn");
        admin.setMotDePasse(passwordEncoder.encode("Admin123!"));
        admin.setCodePin(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);
        admin.setStatut(Statut.ACTIF);
        admin.setDateCreation(LocalDateTime.now());
        admin = utilisateurRepository.save(admin);

        // Créer compte pour admin
        Compte compteAdmin = new Compte();
        compteAdmin.setNumeroCompte("OM" + (System.currentTimeMillis() + 1) % 10000000000L); // Numéro aléatoire pour admin
        compteAdmin.setSolde(1000000.0); // 1M FCFA
        compteAdmin.setTypeCompte(TypeCompte.PRINCIPAL);
        compteAdmin.setStatut(Statut.ACTIF);
        compteAdmin.setUtilisateur(admin);
        compteAdmin.setDateCreation(LocalDateTime.now());
        compteRepository.save(compteAdmin);

        System.out.println("  ✓ Admin: System Admin (771111111) - Solde: 1,000,000 FCFA");
    }
}
