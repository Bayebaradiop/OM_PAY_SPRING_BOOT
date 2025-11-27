# API TRANSFERT PROGRAMMÉ - Documentation Technique

## 📋 Vue d'ensemble

Cette API permet de programmer des transferts d'argent qui seront exécutés automatiquement à une date/heure spécifiée. Un scheduler vérifie toutes les minutes les transferts à exécuter.

---

## 🔐 Authentification

Tous les endpoints nécessitent un token JWT dans le header :
```
Authorization: Bearer {votre_token_jwt}
```

---

## 📡 Endpoints

### 1. POST /api/transferts-programmes

**Description** : Créer un nouveau transfert programmé

**Headers** :
```
Content-Type: application/json
Authorization: Bearer {token}
```

**Body** :
```json
{
  "telephoneDestinataire": "779876543",
  "montant": 1000,
  "dateExecution": "2025-11-27T15:30:00"
}
```

**Paramètres** :
| Champ | Type | Requis | Validation | Description |
|-------|------|--------|------------|-------------|
| telephoneDestinataire | String | Oui | Non vide | Numéro du destinataire |
| montant | Double | Oui | > 0 | Montant en FCFA |
| dateExecution | LocalDateTime | Oui | Futur, < 1 an | Date/heure d'exécution |

**Réponse 201 Created** :
```json
{
  "success": true,
  "message": "Transfert programmé avec succès",
  "data": {
    "montant": 1000.0,
    "dateCreation": "2025-11-27T01:01:04.727334724"
  },
  "timestamp": "2025-11-27T01:01:05"
}
```

**Erreurs possibles** :
- `400 Bad Request` : Date dans le passé ou trop lointaine
- `401 Unauthorized` : Token invalide ou absent
- `404 Not Found` : Utilisateur introuvable

**Exemple cURL** :
```bash
curl -X POST http://localhost:8083/api/transferts-programmes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGci..." \
  -d '{
    "telephoneDestinataire": "779876543",
    "montant": 1000,
    "dateExecution": "2025-11-27T15:30:00"
  }'
```

---

### 2. GET /api/transferts-programmes/mes-transferts

**Description** : Récupérer tous mes transferts programmés (triés par date de création décroissante)

**Headers** :
```
Authorization: Bearer {token}
```

**Réponse 200 OK** :
```json
[
  {
    "id": 1,
    "utilisateurExpediteur": {
      "id": 1,
      "nom": "Diop",
      "prenom": "Moussa",
      "telephone": "771234567",
      "email": "moussa@test.com",
      "role": "CLIENT",
      "statut": "ACTIF"
    },
    "telephoneDestinataire": "779876543",
    "montant": 1000.0,
    "dateExecution": "2025-11-27T15:30:00",
    "statut": "ACTIF",
    "dateCreation": "2025-11-27T01:00:00",
    "dateExecutionReelle": null,
    "messageErreur": null
  }
]
```

**Statuts** :
- `ACTIF` : En attente d'exécution
- `TERMINE` : Exécuté avec succès
- `ANNULE` : Annulé par l'utilisateur
- `ECHOUE` : Échec lors de l'exécution

**Exemple cURL** :
```bash
curl -X GET http://localhost:8083/api/transferts-programmes/mes-transferts \
  -H "Authorization: Bearer eyJhbGci..."
```

---

### 3. DELETE /api/transferts-programmes/{id}

**Description** : Annuler un transfert programmé

**Condition** : Seuls les transferts avec statut `ACTIF` peuvent être annulés

**Headers** :
```
Authorization: Bearer {token}
```

**Paramètre URL** :
- `id` (Long) : Identifiant du transfert à annuler

**Réponse 204 No Content** : Corps vide (succès)

**Erreurs possibles** :
- `400 Bad Request` : Transfert déjà exécuté, annulé ou échoué
- `401 Unauthorized` : Token invalide
- `403 Forbidden` : Tentative d'annuler le transfert d'un autre utilisateur
- `404 Not Found` : Transfert introuvable

**Exemple cURL** :
```bash
curl -X DELETE http://localhost:8083/api/transferts-programmes/1 \
  -H "Authorization: Bearer eyJhbGci..."
```

---

## ⚙️ Fonctionnement du Scheduler

### Mécanisme d'exécution automatique

Un scheduler Spring (`@Scheduled`) s'exécute toutes les **60 secondes** (1 minute) :

```java
@Scheduled(fixedRate = 60000)
public void verifierEtExecuterTransferts()
```

### Processus d'exécution

1. **Recherche** : Récupère tous les transferts avec :
   - Statut = `ACTIF`
   - Date d'exécution ≤ maintenant

2. **Authentification** : Crée un contexte de sécurité temporaire avec les identifiants de l'utilisateur expéditeur

3. **Exécution** : Appelle le service de transfert standard (`transactionService.transfert()`)

4. **Mise à jour** :
   - **Succès** : Statut → `TERMINE`, enregistre la date d'exécution réelle
   - **Échec** : Statut → `ECHOUE`, enregistre le message d'erreur

5. **Nettoyage** : Supprime le contexte de sécurité temporaire

### Logs du scheduler

Le scheduler affiche des logs pour suivre l'exécution :
- 🔄 : Nombre de transferts trouvés
- 💸 : Début d'exécution d'un transfert
- ✅ : Succès
- ❌ : Échec

**Exemple de logs** :
```
🔄 2 transfert(s) programmé(s) à exécuter
💸 Exécution du transfert programmé #1 de 1000.0 FCFA vers 779876543
✅ Transfert programmé #1 exécuté avec succès
```

---

## 🗄️ Structure de la Base de Données

### Table `transfert_programme`

```sql
CREATE TABLE transfert_programme (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_expediteur_id BIGINT NOT NULL,
    telephone_destinataire VARCHAR(20) NOT NULL,
    montant DOUBLE PRECISION NOT NULL,
    date_execution TIMESTAMP NOT NULL,
    statut VARCHAR(20) NOT NULL,
    date_creation TIMESTAMP NOT NULL,
    date_execution_reelle TIMESTAMP,
    message_erreur TEXT,
    CONSTRAINT fk_transfert_programme_utilisateur 
        FOREIGN KEY (utilisateur_expediteur_id) 
        REFERENCES utilisateur(id) 
        ON DELETE CASCADE
);
```

### Index pour optimisation

```sql
-- Index pour le scheduler (recherche des transferts à exécuter)
CREATE INDEX idx_transfert_programme_statut_date 
    ON transfert_programme(statut, date_execution);

-- Index pour les requêtes utilisateur
CREATE INDEX idx_transfert_programme_utilisateur 
    ON transfert_programme(utilisateur_expediteur_id, date_creation DESC);
```

---

## 🏗️ Architecture Technique

### Classes principales

1. **TransfertProgrammeController** : Gestion des endpoints REST
2. **TransfertProgrammeService** : Logique métier et scheduler
3. **TransfertProgrammeRepository** : Accès aux données (JPA)
4. **TransfertProgramme** : Entité JPA (modèle)
5. **StatutTransfertProgramme** : Enum des statuts
6. **TransfertProgrammeRequest** : DTO de requête

### Dépendances

- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- Spring Scheduling
- PostgreSQL
- Lombok

---

## 🧪 Tests

### Test complet avec script

Un script de test automatique est disponible : `test_transfert_programme.sh`

**Exécution** :
```bash
./test_transfert_programme.sh
```

**Ce script teste** :
1. Connexion et récupération du token JWT
2. Calcul de la date d'exécution (2 minutes dans le futur)
3. Création d'un transfert programmé
4. Listage des transferts programmés
5. Attente de l'exécution automatique

### Tests manuels avec cURL

**1. Se connecter** :
```bash
TOKEN=$(curl -s -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"telephone": "771234567", "motDePasse": "Password123!"}' \
  | jq -r '.data.token')
```

**2. Créer un transfert dans 2 minutes** :
```bash
DATE=$(date -d "+2 minutes" "+%Y-%m-%dT%H:%M:%S")
curl -X POST http://localhost:8083/api/transferts-programmes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"telephoneDestinataire\": \"779876543\",
    \"montant\": 1000,
    \"dateExecution\": \"$DATE\"
  }"
```

**3. Lister mes transferts** :
```bash
curl -X GET http://localhost:8083/api/transferts-programmes/mes-transferts \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

**4. Surveiller les logs** :
```bash
tail -f app.log | grep -E "🔄|💸|✅|❌"
```

**5. Vérifier l'exécution en base** :
```bash
psql -h <host> -U <user> -d <db> -c \
  "SELECT id, statut, date_execution, date_execution_reelle 
   FROM transfert_programme 
   ORDER BY id DESC LIMIT 5;"
```

---

## ⚠️ Limitations et Contraintes

1. **Date d'exécution** : Maximum 1 an dans le futur
2. **Précision** : Exécution toutes les minutes (tolérance de ±60 secondes)
3. **Annulation** : Impossible une fois le transfert exécuté
4. **Authentification** : L'expéditeur doit avoir un compte actif au moment de l'exécution
5. **Solde** : Le solde doit être suffisant au moment de l'exécution (pas de vérification à la programmation)

---

## 🔒 Sécurité

1. **Authentification JWT** : Tous les endpoints protégés
2. **Isolation utilisateur** : Chaque utilisateur ne voit que ses propres transferts
3. **Contexte temporaire** : Le scheduler crée un contexte de sécurité limité pour l'exécution
4. **Validation** : Toutes les données entrantes sont validées
5. **Transaction atomique** : L'exécution du transfert est transactionnelle (@Transactional)

---

## 📊 Monitoring

### Métriques à surveiller

1. **Taux d'échec** : Pourcentage de transferts avec statut `ECHOUE`
2. **Latence** : Écart entre date prévue et date d'exécution réelle
3. **Volume** : Nombre de transferts programmés par jour
4. **Charge** : Nombre de transferts à exécuter par cycle du scheduler

### Requêtes utiles

**Statistiques par statut** :
```sql
SELECT statut, COUNT(*) as nombre, SUM(montant) as total_montant
FROM transfert_programme
GROUP BY statut;
```

**Transferts en échec** :
```sql
SELECT id, telephone_destinataire, montant, message_erreur
FROM transfert_programme
WHERE statut = 'ECHOUE'
ORDER BY date_creation DESC
LIMIT 10;
```

**Performance du scheduler** :
```sql
SELECT 
    AVG(EXTRACT(EPOCH FROM (date_execution_reelle - date_execution))) as latence_moyenne_secondes,
    MAX(EXTRACT(EPOCH FROM (date_execution_reelle - date_execution))) as latence_max_secondes
FROM transfert_programme
WHERE statut = 'TERMINE' AND date_execution_reelle IS NOT NULL;
```

---

## 🚀 Évolutions Futures Possibles

1. **Transferts récurrents** : Quotidien, hebdomadaire, mensuel
2. **Notifications** : Email/SMS avant et après exécution
3. **Modification** : Permettre de modifier un transfert programmé avant exécution
4. **Priorité** : Système de priorité pour l'ordre d'exécution
5. **Retry** : Tentatives automatiques en cas d'échec temporaire
6. **Dashboard** : Interface de gestion et statistiques
7. **Export** : Export des transferts programmés en CSV/PDF
8. **Limites** : Nombre maximum de transferts programmés par utilisateur

---

## 📞 Support

Pour toute question ou problème :
- Logs : `tail -f app.log`
- Base de données : Table `transfert_programme`
- Health check : `GET /actuator/health`

---

**Version** : 1.0  
**Date** : 27 Novembre 2025  
**Auteur** : OM Pay Development Team
