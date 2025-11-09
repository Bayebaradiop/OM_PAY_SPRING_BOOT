# 📦 Modèles de données - Om_Pay

## Structure des packages recommandée

```
src/main/java/om/example/om_pay/
├── model/
│   ├── User.java
│   ├── Compte.java
│   ├── Transaction.java
│   ├── Marchand.java
│   ├── GrilleTarification.java
│   ├── Notification.java (optionnel)
│   └── enums/
│       ├── Role.java
│       ├── Statut.java
│       ├── TypeCompte.java
│       ├── TypeTransaction.java
│       ├── StatutTransaction.java
│       └── TypeNotification.java (optionnel)
```

---

## 🧩 Modèles principaux

### 1️⃣ User.java (Utilisateur - Client & Distributeur)

```java
package om.example.om_pay.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utilisateur")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String telephone;

    private String email;

    @Column(nullable = false)
    private String motDePasse;  // À hasher avec BCrypt

    @Column(length = 6, nullable = false)
    private String codePin;  // À hasher aussi

    @Enumerated(EnumType.STRING)
    private Role role;  // CLIENT ou DISTRIBUTEUR

    @Enumerated(EnumType.STRING)
    private Statut statut; // ACTIF ou INACTIF

    private Double plafondQuotidien = 500000.0;  // Limite par jour
    private Double totalTransfertJour = 0.0;     // Suivi des transferts du jour
    private LocalDate dernierResetPlafond;       // Pour réinitialiser chaque jour

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification;

    // Relation : Un utilisateur peut avoir plusieurs comptes
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Compte> comptes;

    // Relation : Un distributeur peut effectuer plusieurs transactions
    @OneToMany(mappedBy = "distributeur")
    private List<Transaction> operationsDistributeur;

    // Constructeurs
    public User() {}

    // Getters & Setters (tous les champs)
}
```

---

### 2️⃣ Compte.java (Compte bancaire)

```java
package om.example.om_pay.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "compte")
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroCompte;  // Ex: OM77123456789

    @Column(nullable = false)
    private Double solde = 0.0;

    private String devise = "XOF";  // Franc CFA

    @Enumerated(EnumType.STRING)
    private TypeCompte typeCompte; // PRINCIPAL, EPARGNE

    @Enumerated(EnumType.STRING)
    private Statut statut; // ACTIF, INACTIF

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification;

    // Relation avec User (propriétaire du compte)
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    // Relations avec transactions
    @OneToMany(mappedBy = "compteExpediteur")
    private List<Transaction> transactionsEnvoyees;

    @OneToMany(mappedBy = "compteDestinataire")
    private List<Transaction> transactionsRecues;

    public Compte() {}

    // Getters & Setters
}
```

---

### 3️⃣ Transaction.java (Toutes les opérations)

```java
package om.example.om_pay.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;  // Ex: TRX20251107123456

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction typeTransaction; // PAIEMENT, DEPOT, RETRAIT, TRANSFERT

    @Column(nullable = false)
    private Double montant;

    private Double frais = 0.0;

    @Column(nullable = false)
    private Double montantTotal;  // montant + frais

    // Compte expéditeur (peut être null pour DEPOT)
    @ManyToOne
    @JoinColumn(name = "compte_expediteur_id")
    private Compte compteExpediteur;

    // Compte destinataire (peut être null pour RETRAIT)
    @ManyToOne
    @JoinColumn(name = "compte_destinataire_id")
    private Compte compteDestinataire;

    // Pour paiement marchand
    @ManyToOne
    @JoinColumn(name = "marchand_id")
    private Marchand marchand;

    // Distributeur qui effectue l'opération (pour DEPOT/RETRAIT)
    @ManyToOne
    @JoinColumn(name = "distributeur_id")
    private User distributeur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransaction statut; // EN_COURS, REUSSI, ECHOUE, ANNULE

    private String description;
    private String messageErreur;  // Si transaction échouée

    @Column(nullable = false)
    private LocalDateTime dateTransaction = LocalDateTime.now();

    private LocalDateTime dateTraitement;  // Quand la transaction a été complétée

    public Transaction() {}

    // Getters & Setters
}
```

---

### 4️⃣ Marchand.java (Commerçants)

```java
package om.example.om_pay.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "marchand")
public class Marchand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomCommercial;

    @Column(unique = true, nullable = false)
    private String numeroMarchand;  // Ex: 77123456789

    @Column(unique = true, nullable = false)
    private String codeMarchand;  // Ex: OM1234 ⭐ CODE POUR PAIEMENT

    private String categorie;  // Restaurant, Boutique, Pharmacie, etc.
    private String adresse;
    private String email;

    @Enumerated(EnumType.STRING)
    private Statut statut;  // ACTIF, INACTIF

    @Column(nullable = false)
    private Double commission = 0.0;  // % de commission sur chaque paiement

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "marchand")
    private List<Transaction> transactions;

    public Marchand() {}

    // Getters & Setters
}
```

**🎯 Flux de paiement marchand :**
1. Client saisit le `codeMarchand` (ex: OM1234)
2. API recherche le marchand par ce code
3. Transaction créée avec référence au marchand
4. Frais calculés selon la grille tarifaire

---

### 5️⃣ GrilleTarification.java (Calcul des frais)

```java
package om.example.om_pay.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grille_tarification")
public class GrilleTarification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction typeTransaction;

    @Column(nullable = false)
    private Double montantMin;

    @Column(nullable = false)
    private Double montantMax;

    @Column(nullable = false)
    private Double frais;

    private Boolean pourcentage = false;  // Si true, frais est un %

    @Enumerated(EnumType.STRING)
    private Statut statut;

    public GrilleTarification() {}

    // Getters & Setters
}
```

---

### 6️⃣ Notification.java (Optionnel - pour historique)

```java
package om.example.om_pay.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(nullable = false)
    private String titre;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;  // TRANSACTION, ALERTE, PROMOTION

    private Boolean lu = false;

    private LocalDateTime dateEnvoi = LocalDateTime.now();

    public Notification() {}

    // Getters & Setters
}
```

---

## 🏷️ Enums nécessaires

### Role.java
```java
package om.example.om_pay.model.enums;

public enum Role {
    CLIENT,
    DISTRIBUTEUR,
    ADMIN  // optionnel pour gestion
}
```

### Statut.java
```java
package om.example.om_pay.model.enums;

public enum Statut {
    ACTIF,
    INACTIF,
    SUSPENDU,
    BLOQUE
}
```

### TypeCompte.java
```java
package om.example.om_pay.model.enums;

public enum TypeCompte {
    PRINCIPAL,
    EPARGNE
}
```

### TypeTransaction.java
```java
package om.example.om_pay.model.enums;

public enum TypeTransaction {
    TRANSFERT,
    DEPOT,
    RETRAIT,
    PAIEMENT,
    RECHARGE  // optionnel
}
```

### StatutTransaction.java
```java
package om.example.om_pay.model.enums;

public enum StatutTransaction {
    EN_COURS,
    REUSSI,
    ECHOUE,
    ANNULE,
    REMBOURSE  // optionnel
}
```

### TypeNotification.java (optionnel)
```java
package om.example.om_pay.model.enums;

public enum TypeNotification {
    TRANSACTION,
    ALERTE,
    PROMOTION,
    SECURITE
}
```

---

## 📊 Résumé des modèles

| Modèle | Rôle | Obligatoire |
|--------|------|-------------|
| **User** | Gestion clients et distributeurs | ✅ Oui |
| **Compte** | Comptes bancaires | ✅ Oui |
| **Transaction** | Toutes les opérations | ✅ Oui |
| **Marchand** | Commerçants pour paiements | ✅ Oui |
| **GrilleTarification** | Calcul automatique des frais | ✅ Oui |
| **Notification** | Historique et alertes | ⚠️ Optionnel |

---

## ✅ Fonctionnalités couvertes

| Fonctionnalité | Possible ? | Remarque |
|----------------|-----------|----------|
| Connexion | ✅ Oui | `telephone` + `motDePasse` (à hasher) |
| Afficher solde | ✅ Oui | `Compte.solde` |
| Historique transactions | ✅ Oui | `transactionsEnvoyees` + `transactionsRecues` |
| Transfert | ✅ Oui | Vérifier solde + codePin + plafond |
| Dépôt distributeur | ✅ Oui | Vérifier rôle DISTRIBUTEUR |
| Retrait distributeur | ✅ Oui | Vérifier solde + rôle + codePin |
| Paiement marchand | ✅ Oui | Via `codeMarchand` |

---

## 🔒 Points de sécurité importants

1. **Hasher les mots de passe** : Utiliser BCrypt pour `motDePasse` et `codePin`
2. **Validation du codePin** : Pour chaque transaction sensible
3. **Gestion des plafonds** : Vérifier `plafondQuotidien` et `totalTransfertJour`
4. **JWT pour l'authentification** : Implémenter Spring Security
5. **Validation des rôles** : Un DISTRIBUTEUR ne peut pas faire certaines opérations CLIENT

---

## 📝 Prochaines étapes

1. Créer les packages et classes
2. Ajouter les dépendances nécessaires dans `pom.xml`:
   - Spring Data JPA
   - Spring Security
   - JWT
   - Base de données (PostgreSQL/MySQL)
3. Implémenter les repositories
4. Créer les services métier
5. Développer les contrôleurs REST
6. Ajouter la validation et la gestion des erreurs
