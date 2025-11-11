# 📋 Guide d'utilisation des Profils Spring Boot

## 🎯 Profils disponibles

### **1. DEV (Développement)** 
- Base de données : PostgreSQL Render (même config)
- DDL : `create-drop` (recrée la base à chaque démarrage)
- Logs : Détaillés (DEBUG)
- Swagger : Activé
- Email : Brevo avec indicateur [DEV]

### **2. PROD (Production)**
- Base de données : PostgreSQL Render (variables d'environnement)
- DDL : `validate` (ne modifie pas la structure)
- Logs : Minimaux (WARN)
- Swagger : Désactivé
- Email : Brevo production

---

## 🚀 Comment activer un profil

### **Méthode 1 : Modifier application.properties**
```properties
# Dans src/main/resources/application.properties
spring.profiles.active=dev    # Pour développement
# ou
spring.profiles.active=prod   # Pour production
```

### **Méthode 2 : Ligne de commande Maven**
```bash
# Démarrer en DEV
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Démarrer en PROD
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### **Méthode 3 : Variable d'environnement**
```bash
# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run

# Windows
set SPRING_PROFILES_ACTIVE=prod
mvnw spring-boot:run
```

### **Méthode 4 : Fichier JAR (Déploiement)**
```bash
# Compiler
./mvnw clean package -DskipTests

# Exécuter en DEV
java -jar target/om_pay-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Exécuter en PROD
java -jar target/om_pay-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### **Méthode 5 : Déploiement Render**
Dans Render Dashboard → Environment Variables :
```
SPRING_PROFILES_ACTIVE=prod
```

---

## 🔐 Variables d'environnement PRODUCTION

Pour le profil **PROD**, définir ces variables d'environnement sur Render :

```bash
# Base de données (automatique sur Render)
DATABASE_URL=jdbc:postgresql://...
DATABASE_USERNAME=om_pay_db_user
DATABASE_PASSWORD=PhVa2xi4B20BdgrxeyEvxiJF13SCRBh6

# JWT (IMPORTANT : Changer en production)
JWT_SECRET=VotreCléSécuriséePourLaProduction...
JWT_EXPIRATION=86400000

# Email Brevo
BREVO_API_KEY=xkeysib-...
MAIL_FROM_ADDRESS=noreply@ompay.sn
MAIL_FROM_NAME=OM Pay

# Profil actif
SPRING_PROFILES_ACTIVE=prod
```

---

## 📊 Différences entre profils

| Configuration | DEV | PROD |
|--------------|-----|------|
| **DDL Auto** | create-drop | validate |
| **Show SQL** | true | false |
| **Logs** | DEBUG | WARN |
| **Swagger** | ✅ Activé | ❌ Désactivé |
| **Port** | 8083 | 8080 |
| **Logs fichier** | om_pay-dev.log | om_pay-prod.log |
| **Erreurs détaillées** | ✅ Oui | ❌ Non (sécurité) |

---

## ⚠️ Recommandations

### **Développement (DEV)**
- Utiliser `create-drop` pour tests rapides
- Logs détaillés pour debugging
- Swagger accessible pour tester l'API

### **Production (PROD)**
- **OBLIGATOIRE** : Utiliser `validate` ou `update` (jamais `create-drop`)
- Définir les variables d'environnement sensibles
- Désactiver Swagger
- Logs minimaux pour performance

---

## 🧪 Vérifier le profil actif

```bash
# Dans les logs au démarrage, chercher :
"The following 1 profile is active: dev"
# ou
"The following 1 profile is active: prod"
```

---

## 📝 Fichiers de configuration

```
src/main/resources/
├── application.properties           # Configuration commune
├── application-dev.properties       # Surcharge pour DEV
└── application-prod.properties      # Surcharge pour PROD
```

---

## 🔄 Changement rapide de profil

```bash
# Actuellement en DEV, passer en PROD :
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Revenir en DEV :
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
