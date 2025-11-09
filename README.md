# 📱 Orange Money - Application de Paiement Mobile

## 📋 Table des matières
- [Description](#description)
- [Technologies utilisées](#technologies-utilisées)
- [Structure du projet par fonctionnalités](#structure-du-projet-par-fonctionnalités)
- [Guide de création du projet](#guide-de-création-du-projet)
- [Configuration](#configuration)
- [Tests de l'API](#tests-de-lapi)
- [Points critiques](#points-critiques)

---

## Description

Application de gestion de transactions mobiles Orange Money permettant:
- **Dépôts** et **retraits** d'argent via distributeurs
- **Transferts** entre clients
- **Paiements** chez les marchands
- Gestion des utilisateurs (CLIENT, DISTRIBUTEUR, MARCHAND, ADMIN)
- Authentification JWT sécurisée

---

## Technologies utilisées

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** + JWT (HS512)
- **Spring Data JPA** + Hibernate
- **PostgreSQL 15**
- **Maven**
- **BCrypt** pour le hachage des mots de passe

---

## Structure du projet par fonctionnalités

### 🔐 **1. AUTHENTIFICATION & SÉCURITÉ**

#### Configuration initiale
```
pom.xml                                    # Dépendances Maven
src/main/resources/
  └── application.properties               # Configuration BDD, JWT, Port
```

#### Security & JWT
```
src/main/java/om/example/om_pay/security/
  ├── JwtUtil.java                         # Génération et validation JWT (HS512, 512-bit)
  ├── JwtAuthenticationFilter.java         # Filtre pour extraire et valider le token
  ├── CustomUserDetailsService.java        # Chargement utilisateur pour Spring Security
  └── SecurityConfig.java                  # Configuration globale (endpoints publics/privés)
```

#### Services d'authentification
```
src/main/java/om/example/om_pay/
  ├── interfaces/
  │   └── IAuthService.java                # Interface du service d'auth
  └── service/impl/
      └── AuthServiceImpl.java             # Inscription, Login, Génération JWT
```

#### Controller
```
src/main/java/om/example/om_pay/controller/
  └── AuthController.java                  # POST /api/auth/register, /api/auth/login
```

#### DTOs
```
src/main/java/om/example/om_pay/dto/
  ├── request/
  │   ├── RegisterRequest.java             # Inscription (nom, prenom, tel, email, mdp, pin, role)
  │   └── LoginRequest.java                # Connexion (telephone, motDePasse)
  └── response/
      ├── AuthResponse.java                # Réponse avec token JWT
      └── ApiResponse.java                 # Réponse générique (success, message, data)
```

---

### 👥 **2. GESTION DES UTILISATEURS**

#### Modèle
```
src/main/java/om/example/om_pay/model/
  └── Utilisateur.java                     # Entité (id, nom, prenom, tel, email, mdp, pin, role, statut)
                                           # ⚠️ motDePasse et codePin: length=255 (BCrypt)
```

#### Repository
```
src/main/java/om/example/om_pay/repository/
  └── UtilisateurRepository.java           # findByTelephone(), existsByTelephone()
```

#### Service
```
src/main/java/om/example/om_pay/
  ├── interfaces/
  │   └── IUtilisateurService.java         # Interface CRUD utilisateur
  └── service/impl/
      └── UtilisateurServiceImpl.java      # CRUD, changeCodePin(), verifyCodePin(), mapToResponse()
```

#### Controller
```
src/main/java/om/example/om_pay/controller/
  └── UtilisateurController.java           # 8 endpoints:
                                           # GET /me, /{id}, /all
                                           # PUT /update/{id}, /bloquer/{id}, /debloquer/{id}
                                           # PUT /change-pin
                                           # DELETE /{id}
```

#### DTOs
```
src/main/java/om/example/om_pay/dto/
  ├── request/
  │   └── UpdateUtilisateurRequest.java    # Mise à jour profil
  └── response/
      └── UtilisateurResponse.java         # Utilisateur sans mot de passe/PIN
```

---

### 💳 **3. GESTION DES COMPTES**

#### Modèle
```
src/main/java/om/example/om_pay/model/
  └── Compte.java                          # Entité (id, numeroCompte, solde, typeCompte, statut)
                                           # Relation: ManyToOne avec Utilisateur
```

#### Repository
```
src/main/java/om/example/om_pay/repository/
  └── CompteRepository.java                # findByNumeroCompte(), findByUtilisateurId()
```

#### Service
```
src/main/java/om/example/om_pay/
  ├── interfaces/
  │   └── ICompteService.java              # Interface gestion comptes
  └── service/impl/
      └── CompteServiceImpl.java           # Création, crédit, débit, bloquer, débloquer
                                           # consulterSolde(), getComptesByUtilisateur()
```

#### Controller
```
src/main/java/om/example/om_pay/controller/
  └── CompteController.java                # 4 endpoints:
                                           # GET /solde/{numeroCompte}
                                           # GET /utilisateur/{utilisateurId}
                                           # PUT /bloquer/{numeroCompte}
                                           # PUT /debloquer/{numeroCompte}
```

#### DTOs
```
src/main/java/om/example/om_pay/dto/response/
  └── CompteResponse.java                  # DTO compte (sans relations)
```

---

### 💰 **4. GESTION DES TRANSACTIONS**

#### Modèles
```
src/main/java/om/example/om_pay/model/
  ├── Transaction.java                     # Entité (id, reference, type, montant, frais, statut)
  │                                        # ⚠️ Utiliser dateTransaction (pas dateCreation)
  │                                        # Relations: ManyToOne avec Compte, Marchand
  └── Marchand.java                        # Entité (id, nomCommercial, codeMarchand, commission)
                                           # ⚠️ Utiliser nomCommercial (pas nom)
```

#### Repositories
```
src/main/java/om/example/om_pay/repository/
  ├── TransactionRepository.java           # findByReference(), findByCompteId()
  │                                        # findByCompteIdAndDateBetween() avec dateTransaction
  └── MarchandRepository.java              # findByCodeMarchand(), findByNomCommercial()
```

#### Service
```
src/main/java/om/example/om_pay/
  ├── interfaces/
  │   └── ITransactionService.java         # Interface transactions
  └── service/impl/
      └── TransactionServiceImpl.java      # ⭐ SERVICE PRINCIPAL (409 lignes)
                                           # 
                                           # transfert() → 100 FCFA de frais
                                           # depot() → 0 FCFA de frais
                                           # retrait() → 500 FCFA de frais
                                           # paiement() → 1.5% de frais
                                           # 
                                           # Vérifications:
                                           # - Code PIN client
                                           # - Solde suffisant
                                           # - Plafond quotidien
                                           # - Statut compte actif
                                           # 
                                           # annuler(), getHistorique(), getHistoriqueByPeriode()
```

#### Controller
```
src/main/java/om/example/om_pay/controller/
  └── TransactionController.java           # 7 endpoints:
                                           # POST /transfert (CLIENT → CLIENT)
                                           # POST /depot (DISTRIBUTEUR → CLIENT)
                                           # POST /retrait (CLIENT → DISTRIBUTEUR)
                                           # POST /paiement (CLIENT → MARCHAND)
                                           # GET /historique/{numeroCompte}
                                           # GET /historique/{numeroCompte}/periode
                                           # PUT /annuler/{reference}
```

#### DTOs
```
src/main/java/om/example/om_pay/dto/
  ├── request/
  │   ├── TransfertRequest.java            # telephoneDestinataire, montant, codePin
  │   ├── DepotRequest.java                # telephoneClient, montant, codePin (distributeur)
  │   ├── RetraitRequest.java              # telephoneClient, montant, codePin (client)
  │   └── PaiementRequest.java             # codeMarchand, montant, codePin
  └── response/
      └── TransactionResponse.java         # Transaction complète avec référence
```

---

### ✅ **5. VALIDATIONS PERSONNALISÉES**

```
src/main/java/om/example/om_pay/validations/
  ├── annotations/
  │   ├── ValidTelephone.java              # Format: 77/78/76/70/75 + 7 chiffres
  │   ├── ValidCodePin.java                # 6 chiffres non séquentiels (pas 123456, 111111)
  │   └── ValidMontant.java                # Montant min/max dynamique
  └── validators/
      ├── TelephoneValidator.java          # Implémentation validation téléphone
      ├── CodePinValidator.java            # Implémentation validation PIN
      └── MontantValidator.java            # Implémentation validation montant
```

**Utilisation:**
```java
public class TransfertRequest {
    @ValidTelephone
    private String telephoneDestinataire;
    
    @ValidMontant(min = 100, max = 1000000)
    private Double montant;
    
    @ValidCodePin
    private String codePin;
}
```

---

### 🚨 **6. GESTION DES ERREURS**

```
src/main/java/om/example/om_pay/exception/
  ├── ResourceNotFoundException.java       # Entité non trouvée (404)
  ├── BadRequestException.java             # Requête invalide (400)
  ├── UnauthorizedException.java           # Non autorisé (401)
  └── GlobalExceptionHandler.java          # @RestControllerAdvice
                                           # Capture toutes les exceptions
                                           # Retourne ApiResponse avec message d'erreur
```

---

### 📊 **7. ENUMS (Types de données)**

```
src/main/java/om/example/om_pay/model/enums/
  ├── Role.java                            # CLIENT, DISTRIBUTEUR, MARCHAND, ADMIN
  ├── Statut.java                          # ACTIF, INACTIF, SUSPENDU, BLOQUE
  ├── TypeCompte.java                      # PRINCIPAL, EPARGNE
  ├── TypeTransaction.java                 # DEPOT, RETRAIT, TRANSFERT, PAIEMENT
  └── StatutTransaction.java               # EN_ATTENTE, REUSSI, ECHOUE, ANNULE
                                           # ⚠️ Utiliser REUSSI (pas REUSSIE)
```

---

### 🎯 **8. APPLICATION PRINCIPALE**

```
src/main/java/om/example/om_pay/
  └── OmPayApplication.java                # @SpringBootApplication
                                           # Point d'entrée main()
```

---

## Guide de création du projet

### **Étape 1: Initialisation Maven**

```bash
mkdir om_pay && cd om_pay
touch pom.xml
```

**Créer `pom.xml`** avec:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- PostgreSQL Driver
- JWT (io.jsonwebtoken:jjwt-api:0.12.3)
- BCrypt (inclus dans Spring Security)

---

### **Étape 2: Configuration**

**Créer `src/main/resources/application.properties`:**

```properties
# Application
spring.application.name=om_pay
server.port=8083

# PostgreSQL
spring.datasource.url=jdbc:postgresql://127.0.0.1:5433/om_pay_db
spring.datasource.username=admin
spring.datasource.password=admin123
spring.jpa.hibernate.ddl-auto=update

# JWT (⚠️ Minimum 104 caractères pour 512-bit HS512)
jwt.secret=OmPaySecretKey2025VerySecureAndLongKeyForProductionUseOnlyWithExtraCharactersToMeet512BitsRequirement
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```

---

### **Étape 3: Enums** (Créer en premier)

1. `Role.java`
2. `Statut.java`
3. `TypeCompte.java`
4. `TypeTransaction.java`
5. `StatutTransaction.java`

---

### **Étape 4: Modèles/Entités**

**Ordre de création:**

1. **`Utilisateur.java`** (indépendant)
   ```java
   @Column(length=255, nullable=false) // ⚠️ IMPORTANT pour BCrypt
   private String motDePasse;
   
   @Column(length=255, nullable=false)
   private String codePin;
   ```

2. **`Compte.java`** (dépend de Utilisateur)

3. **`Marchand.java`** (indépendant)

4. **`Transaction.java`** (dépend de Compte et Marchand)
   ```java
   private LocalDateTime dateTransaction; // ⚠️ Pas dateCreation
   ```

---

### **Étape 5: Repositories**

1. `UtilisateurRepository.java`
2. `CompteRepository.java` (ajouter `findByUtilisateurId()`)
3. `MarchandRepository.java` (utiliser `findByNomCommercial()`)
4. `TransactionRepository.java` (utiliser `dateTransaction` dans @Query)

---

### **Étape 6: Exceptions**

1. `ResourceNotFoundException.java`
2. `BadRequestException.java`
3. `UnauthorizedException.java`
4. `GlobalExceptionHandler.java`

---

### **Étape 7: DTOs**

**Request DTOs:**
1. `RegisterRequest.java`
2. `LoginRequest.java`
3. `TransfertRequest.java`
4. `DepotRequest.java`
5. `RetraitRequest.java`
6. `PaiementRequest.java`
7. `UpdateUtilisateurRequest.java`

**Response DTOs:**
1. `ApiResponse.java` (générique)
2. `AuthResponse.java`
3. `TransactionResponse.java`
4. `CompteResponse.java`
5. `UtilisateurResponse.java`

---

### **Étape 8: Validations**

**Annotations:**
1. `@ValidTelephone`
2. `@ValidCodePin`
3. `@ValidMontant`

**Validators:**
1. `TelephoneValidator.java`
2. `CodePinValidator.java`
3. `MontantValidator.java`

---

### **Étape 9: Security**

**Ordre de création:**

1. `JwtUtil.java` (génération/validation token)
2. `CustomUserDetailsService.java` (chargement utilisateur)
3. `JwtAuthenticationFilter.java` (filtre requêtes)
4. `SecurityConfig.java` (configuration finale)

---

### **Étape 10: Services (Interfaces puis Implémentations)**

**Interfaces:**
1. `IAuthService.java`
2. `ICompteService.java`
3. `IUtilisateurService.java`
4. `ITransactionService.java`

**Implémentations:**
1. `AuthServiceImpl.java`
2. `CompteServiceImpl.java`
3. `UtilisateurServiceImpl.java` (avec `mapToResponse()` pour éviter lazy loading)
4. `TransactionServiceImpl.java` (le plus complexe - à faire en dernier)

---

### **Étape 11: Controllers**

1. `AuthController.java`
2. `CompteController.java` (avec `mapToResponse()` dans les méthodes)
3. `UtilisateurController.java` (avec `mapToResponse()` dans les méthodes)
4. `TransactionController.java`

---

### **Étape 12: Application principale**

`OmPayApplication.java`

---

### **Étape 13: Base de données**

```bash
# Créer la base de données PostgreSQL
psql -U admin -h 127.0.0.1 -p 5433
CREATE DATABASE om_pay_db;
\q
```

---

### **Étape 14: Démarrage**

```bash
# Compilation et démarrage
./mvnw clean install
./mvnw spring-boot:run

# Ou en arrière-plan
nohup ./mvnw spring-boot:run > /tmp/om_pay.log 2>&1 &
```

---

## Configuration

### Base de données PostgreSQL

```sql
-- Créer la base de données
CREATE DATABASE om_pay_db;

-- Créer un marchand pour les tests
INSERT INTO marchand (nom_commercial, numero_marchand, code_marchand, categorie, adresse, email, statut, commission) 
VALUES ('Boutique Chez Amadou', '771112233', 'SHOP001', 'Commerce', 'Dakar, Plateau', 'amadou@shop.sn', 'ACTIF', 1.5);
```

### Variables d'environnement (optionnel)

```bash
export DATABASE_URL=jdbc:postgresql://127.0.0.1:5433/om_pay_db
export DATABASE_USERNAME=admin
export DATABASE_PASSWORD=admin123
export JWT_SECRET=VotreCléSecrèteSuper LongueAvecAuMoins104CaractèresPourGarantir512BitsDeSecurite...
```

---

## Tests de l'API

### 1. Inscription CLIENT

```bash
curl -X POST http://localhost:8083/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Diop",
    "prenom": "Moussa",
    "telephone": "771234567",
    "email": "moussa@test.com",
    "motDePasse": "Password123!",
    "codePin": "482915",
    "role": "CLIENT"
  }'
```

**Réponse:**
```json
{
  "success": true,
  "message": "Inscription réussie. Bienvenue sur Orange Money !",
  "data": {
    "token": "eyJhbGci...",
    "telephone": "771234567",
    "nom": "Diop",
    "prenom": "Moussa",
    "role": "CLIENT"
  }
}
```

---

### 2. Inscription DISTRIBUTEUR

```bash
curl -X POST http://localhost:8083/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Ndiaye",
    "prenom": "Abdou",
    "telephone": "775551234",
    "email": "abdou@test.com",
    "motDePasse": "Distrib123!",
    "codePin": "285739",
    "role": "DISTRIBUTEUR"
  }'
```

---

### 3. Login

```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "771234567",
    "motDePasse": "Password123!"
  }'
```

**Récupérer le token:**
```bash
TOKEN=$(curl -s -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"telephone": "771234567", "motDePasse": "Password123!"}' \
  | jq -r '.data.token')
```

---

### 4. Dépôt (DISTRIBUTEUR vers CLIENT)

**⚠️ Prérequis: Créditer le compte distributeur via SQL**

```sql
UPDATE compte SET solde = 500000 
WHERE utilisateur_id = (SELECT id FROM utilisateur WHERE telephone = '775551234');
```

**Requête API:**

```bash
curl -X POST http://localhost:8083/api/transactions/depot \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_DISTRIBUTEUR" \
  -d '{
    "telephoneClient": "771234567",
    "montant": 50000,
    "codePin": "285739"
  }'
```

**Réponse:**
```json
{
  "success": true,
  "message": "Dépôt effectué avec succès",
  "data": {
    "id": 1,
    "reference": "TRXCC41124F",
    "typeTransaction": "DEPOT",
    "montant": 50000.0,
    "frais": 0.0,
    "montantTotal": 50000.0,
    "statut": "REUSSI"
  }
}
```

---

### 5. Transfert (CLIENT vers CLIENT)

```bash
curl -X POST http://localhost:8083/api/transactions/transfert \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_CLIENT" \
  -d '{
    "telephoneDestinataire": "779876543",
    "montant": 5000,
    "codePin": "482915"
  }'
```

**Frais: 100 FCFA** → Montant total débité: 5,100 FCFA

---

### 6. Retrait (CLIENT via DISTRIBUTEUR)

```bash
curl -X POST http://localhost:8083/api/transactions/retrait \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_DISTRIBUTEUR" \
  -d '{
    "telephoneClient": "771234567",
    "montant": 10000,
    "codePin": "482915"
  }'
```

**⚠️ Important:**
- Token = DISTRIBUTEUR
- codePin = PIN du CLIENT (pas du distributeur)

**Frais: 500 FCFA** → Montant total débité: 10,500 FCFA

---

### 7. Paiement (CLIENT vers MARCHAND)

```bash
curl -X POST http://localhost:8083/api/transactions/paiement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_CLIENT" \
  -d '{
    "codeMarchand": "SHOP001",
    "montant": 15000,
    "codePin": "482915"
  }'
```

**Frais: 1.5% = 225 FCFA** → Montant total débité: 15,225 FCFA

---

### 8. Consulter le solde

```bash
curl -X GET "http://localhost:8083/api/comptes/solde/OM8000380279" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 9. Historique des transactions

```bash
curl -X GET "http://localhost:8083/api/transactions/historique/OM8000380279" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 10. Historique par période

```bash
curl -X GET "http://localhost:8083/api/transactions/historique/OM8000380279/periode?dateDebut=2025-11-08T00:00:00&dateFin=2025-11-08T23:59:59" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 11. Informations utilisateur connecté

```bash
curl -X GET "http://localhost:8083/api/utilisateurs/me" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 12. Liste des comptes d'un utilisateur

```bash
curl -X GET "http://localhost:8083/api/comptes/utilisateur/2" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Points critiques

### ⚠️ **Erreurs courantes à éviter**

#### 1. **Longueur des champs mot de passe et PIN**
```java
// ❌ MAUVAIS
@Column(nullable = false)
private String motDePasse;

@Column(length = 6)
private String codePin;

// ✅ BON (BCrypt nécessite ~60 caractères)
@Column(length = 255, nullable = false)
private String motDePasse;

@Column(length = 255, nullable = false)
private String codePin;
```

---

#### 2. **JWT Secret trop court**
```properties
# ❌ MAUVAIS (464 bits < 512 bits requis pour HS512)
jwt.secret=OmPaySecretKey2025VerySecureAndLongKeyForProductionUseOnly

# ✅ BON (104+ caractères = 832+ bits)
jwt.secret=OmPaySecretKey2025VerySecureAndLongKeyForProductionUseOnlyWithExtraCharactersToMeet512BitsRequirement
```

---

#### 3. **Nom des champs dans Transaction**
```java
// ❌ MAUVAIS
private LocalDateTime dateCreation;

// ✅ BON
private LocalDateTime dateTransaction;

// Dans TransactionRepository:
@Query("SELECT t FROM Transaction t WHERE t.compte.id = :compteId AND t.dateTransaction BETWEEN :debut AND :fin")
```

---

#### 4. **Nom des champs dans Marchand**
```java
// ❌ MAUVAIS
private String nom;

// ✅ BON
private String nomCommercial;

// Dans MarchandRepository:
Optional<Marchand> findByNomCommercial(String nomCommercial);
```

---

#### 5. **Enum StatutTransaction**
```java
// ❌ MAUVAIS
StatutTransaction.REUSSIE

// ✅ BON
StatutTransaction.REUSSI
```

---

#### 6. **Lazy Loading dans les Controllers**
```java
// ❌ MAUVAIS (provoque LazyInitializationException)
@GetMapping("/me")
public ResponseEntity<ApiResponse<Utilisateur>> getCurrentUser() {
    Utilisateur utilisateur = utilisateurService.getCurrentUser();
    return ResponseEntity.ok(new ApiResponse<>(true, "OK", utilisateur));
}

// ✅ BON (utiliser DTO)
@GetMapping("/me")
public ResponseEntity<ApiResponse<UtilisateurResponse>> getCurrentUser() {
    Utilisateur utilisateur = utilisateurService.getCurrentUser();
    UtilisateurResponse response = mapToResponse(utilisateur);
    return ResponseEntity.ok(new ApiResponse<>(true, "OK", response));
}

// Méthode utilitaire dans le controller
private UtilisateurResponse mapToResponse(Utilisateur utilisateur) {
    UtilisateurResponse response = new UtilisateurResponse();
    response.setId(utilisateur.getId());
    response.setNom(utilisateur.getNom());
    response.setPrenom(utilisateur.getPrenom());
    response.setTelephone(utilisateur.getTelephone());
    response.setEmail(utilisateur.getEmail());
    response.setRole(utilisateur.getRole());
    response.setStatut(utilisateur.getStatut());
    response.setDateCreation(utilisateur.getDateCreation());
    return response;
}
```

---

#### 7. **Repository CompteRepository incomplet**
```java
// ❌ MAUVAIS (méthode manquante)
public interface CompteRepository extends JpaRepository<Compte, Long> {
    Optional<Compte> findByNumeroCompte(String numeroCompte);
}

// ✅ BON (ajouter findByUtilisateurId)
public interface CompteRepository extends JpaRepository<Compte, Long> {
    Optional<Compte> findByNumeroCompte(String numeroCompte);
    List<Compte> findByUtilisateurId(Long utilisateurId); // ⭐ IMPORTANT
}

// Dans CompteServiceImpl:
@Override
@Transactional(readOnly = true)
public List<CompteResponse> getComptesByUtilisateur(Long utilisateurId) {
    List<Compte> comptes = compteRepository.findByUtilisateurId(utilisateurId);
    return comptes.stream().map(this::mapToResponse).collect(Collectors.toList());
}
```

---

### 🎯 **Frais par type de transaction**

| Type Transaction | Frais | Total débité |
|-----------------|-------|--------------|
| **DEPOT** | 0 FCFA | Montant |
| **TRANSFERT** | 100 FCFA | Montant + 100 |
| **RETRAIT** | 500 FCFA | Montant + 500 |
| **PAIEMENT** | 1.5% | Montant × 1.015 |

---

### 📊 **Plafonds quotidiens**

| Rôle | Plafond |
|------|---------|
| **CLIENT** | 1 000 000 FCFA |
| **DISTRIBUTEUR** | 5 000 000 FCFA |

---

### 🔒 **Endpoints publics vs privés**

**Publics (sans authentification):**
- `POST /api/auth/register`
- `POST /api/auth/login`

**Privés (JWT requis):**
- Tous les autres endpoints (`/api/transactions/*`, `/api/comptes/*`, `/api/utilisateurs/*`)

---

### 🧪 **Scénario de test complet**

```bash
# 1. Créer CLIENT 1
curl -X POST http://localhost:8083/api/auth/register -H "Content-Type: application/json" \
  -d '{"nom":"Diop","prenom":"Moussa","telephone":"771234567","email":"moussa@test.com","motDePasse":"Password123!","codePin":"482915","role":"CLIENT"}'

# 2. Créer CLIENT 2
curl -X POST http://localhost:8083/api/auth/register -H "Content-Type: application/json" \
  -d '{"nom":"Sarr","prenom":"Fatou","telephone":"779876543","email":"fatou@test.com","motDePasse":"Pass123!","codePin":"789456","role":"CLIENT"}'

# 3. Créer DISTRIBUTEUR
curl -X POST http://localhost:8083/api/auth/register -H "Content-Type: application/json" \
  -d '{"nom":"Ndiaye","prenom":"Abdou","telephone":"775551234","email":"abdou@test.com","motDePasse":"Distrib123!","codePin":"285739","role":"DISTRIBUTEUR"}'

# 4. Créditer le distributeur (SQL)
psql -U admin -h 127.0.0.1 -p 5433 -d om_pay_db -c "UPDATE compte SET solde = 500000 WHERE utilisateur_id = (SELECT id FROM utilisateur WHERE telephone = '775551234');"

# 5. Login DISTRIBUTEUR
TOKEN_DIST=$(curl -s -X POST http://localhost:8083/api/auth/login -H "Content-Type: application/json" \
  -d '{"telephone":"775551234","motDePasse":"Distrib123!"}' | jq -r '.data.token')

# 6. Dépôt 50,000 FCFA sur compte Moussa
curl -X POST http://localhost:8083/api/transactions/depot -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_DIST" \
  -d '{"telephoneClient":"771234567","montant":50000,"codePin":"285739"}'
# Solde Moussa: 50,000 FCFA

# 7. Login CLIENT 1 (Moussa)
TOKEN_MOUSSA=$(curl -s -X POST http://localhost:8083/api/auth/login -H "Content-Type: application/json" \
  -d '{"telephone":"771234567","motDePasse":"Password123!"}' | jq -r '.data.token')

# 8. Transfert 5,000 FCFA de Moussa vers Fatou
curl -X POST http://localhost:8083/api/transactions/transfert -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_MOUSSA" \
  -d '{"telephoneDestinataire":"779876543","montant":5000,"codePin":"482915"}'
# Solde Moussa: 44,900 FCFA (50,000 - 5,100)
# Solde Fatou: 5,000 FCFA

# 9. Retrait 10,000 FCFA de Moussa via DISTRIBUTEUR
curl -X POST http://localhost:8083/api/transactions/retrait -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_DIST" \
  -d '{"telephoneClient":"771234567","montant":10000,"codePin":"482915"}'
# Solde Moussa: 34,400 FCFA (44,900 - 10,500)

# 10. Paiement 15,000 FCFA chez marchand SHOP001
curl -X POST http://localhost:8083/api/transactions/paiement -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_MOUSSA" \
  -d '{"codeMarchand":"SHOP001","montant":15000,"codePin":"482915"}'
# Solde Moussa: 19,175 FCFA (34,400 - 15,225)

# 11. Vérifier le solde final
curl -X GET "http://localhost:8083/api/comptes/solde/OM8000380279" \
  -H "Authorization: Bearer $TOKEN_MOUSSA" | jq '.data'
# Résultat: 19175.0

# 12. Vérifier l'historique
curl -X GET "http://localhost:8083/api/transactions/historique/OM8000380279" \
  -H "Authorization: Bearer $TOKEN_MOUSSA" | jq '.data | length'
# Résultat: 4 transactions
```

**Résultat attendu:**
```
Dépôt:     +50,000 (frais 0)      = +50,000
Transfert:  -5,000 (frais 100)    = -5,100
Retrait:   -10,000 (frais 500)    = -10,500
Paiement:  -15,000 (frais 225)    = -15,225
─────────────────────────────────────────────
SOLDE FINAL:                        19,175 FCFA ✅
```

---

## 🚀 Démarrage rapide

```bash
# 1. Cloner le projet
git clone <repo-url>
cd om_pay

# 2. Créer la base de données
psql -U admin -h 127.0.0.1 -p 5433
CREATE DATABASE om_pay_db;
\q

# 3. Compiler et démarrer
./mvnw clean install
./mvnw spring-boot:run

# 4. Vérifier que l'application est démarrée
curl http://localhost:8083/actuator/health
```

---

## 📞 Support

Pour toute question ou problème, consulter:
- La documentation Spring Boot: https://spring.io/projects/spring-boot
- La documentation JWT: https://jwt.io/
- La documentation PostgreSQL: https://www.postgresql.org/docs/

---

## 📝 Licence

Ce projet est développé à des fins éducatives.

---

**Dernière mise à jour:** 8 novembre 2025
# OM_PAY_SPRING_BOOT
