# Guide de Configuration - Swagger & Déploiement

## 📚 Documentation Swagger/OpenAPI

### Accès à l'interface Swagger UI

L'API est documentée avec Swagger/OpenAPI 3.0. Vous pouvez accéder à l'interface interactive :

#### En développement local
- **Swagger UI** : http://localhost:8083/swagger-ui.html
- **API Docs JSON** : http://localhost:8083/v3/api-docs

#### En production (Render)
- **Swagger UI** : https://om-pay-spring-boot-1.onrender.com/swagger-ui.html
- **API Docs JSON** : https://om-pay-spring-boot-1.onrender.com/v3/api-docs

### Configuration Swagger

#### 1. Mise à jour effectuée

✅ **Dépendance mise à jour dans `pom.xml`**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

✅ **Nouvelle classe de configuration** : `SwaggerConfig.java`
- Configuration moderne avec OpenAPI 3.0
- Authentification JWT intégrée
- Documentation enrichie avec emojis et tableaux
- Support multi-serveurs (local + production)

✅ **Propriétés configurables** dans `application.properties`
```properties
springdoc.api-docs.enabled=${SWAGGER_ENABLED:true}
springdoc.swagger-ui.enabled=${SWAGGER_ENABLED:true}
swagger.server.prod.url=${SWAGGER_SERVER_PROD_URL:https://om-pay-spring-boot-1.onrender.com}
swagger.server.local.url=${SWAGGER_SERVER_LOCAL_URL:http://localhost:8083}
```

#### 2. Utilisation de Swagger UI

##### Étape 1 : Se connecter
1. Ouvrez Swagger UI dans votre navigateur
2. Trouvez l'endpoint **POST /api/auth/login** dans la section "Authentification"
3. Cliquez sur "Try it out"
4. Entrez les identifiants d'un utilisateur de test :
   ```json
   {
     "telephone": "771234567",
     "motDePasse": "Password123!"
   }
   ```
5. Cliquez sur "Execute"
6. Copiez le **token JWT** depuis la réponse

##### Étape 2 : S'authentifier
1. Cliquez sur le bouton **"Authorize"** 🔓 en haut de la page
2. Collez le token JWT (sans "Bearer ")
3. Cliquez sur **"Authorize"** puis **"Close"**
4. Vous êtes maintenant authentifié ! 🎉

##### Étape 3 : Tester les endpoints
- Tous les endpoints protégés sont maintenant accessibles
- Le token est automatiquement ajouté dans l'en-tête Authorization
- Testez les transactions, comptes, utilisateurs, etc.

### Comptes de test disponibles

| Rôle | Nom | Téléphone | Mot de passe | Numéro de compte |
|------|-----|-----------|--------------|------------------|
| CLIENT | Moussa Diop | 771234567 | Password123! | OM8000380279 |
| CLIENT | Fatou Sarr | 779876543 | Pass123! | OM2665616523 |
| CLIENT | Cheikh Fall | 776543210 | Client123! | OM5432147504 |
| DISTRIBUTEUR | Abdou Ndiaye | 775551234 | Distrib123! | OM4274060223 |

---

## 🚀 Variables de Déploiement

### Configuration Render

Le fichier `render.yaml` a été mis à jour avec toutes les variables d'environnement nécessaires :

#### Variables obligatoires (à configurer dans Render Dashboard)

##### Base de données
```yaml
DATABASE_URL          # URL de connexion PostgreSQL
DATABASE_USERNAME     # Nom d'utilisateur de la base
DATABASE_PASSWORD     # Mot de passe de la base
```

##### Sécurité JWT
```yaml
JWT_SECRET           # Clé secrète pour signer les tokens JWT (min 512 bits)
JWT_EXPIRATION       # Durée de validité (défaut: 86400000 = 24h)
```

##### Email (Brevo)
```yaml
BREVO_API_KEY        # Clé API Brevo pour l'envoi d'emails
MAIL_FROM_ADDRESS    # Adresse email expéditrice
MAIL_FROM_NAME       # Nom de l'expéditeur
```

#### Variables optionnelles (valeurs par défaut)

```yaml
SWAGGER_ENABLED              # Activer Swagger (true/false)
SWAGGER_SERVER_PROD_URL      # URL du serveur de production
SWAGGER_SERVER_LOCAL_URL     # URL du serveur local
SPRING_PROFILES_ACTIVE       # Profil Spring (dev/prod)
PORT                         # Port du serveur (8080)
```

### Configuration des profils

#### Profil `dev` (développement)
- Base de données : Render PostgreSQL (ou locale)
- JPA : `create-drop` (recrée la base à chaque démarrage)
- Logs : DEBUG
- Swagger : **activé**
- Données de test : **chargées automatiquement**

#### Profil `prod` (production)
- Base de données : Render PostgreSQL
- JPA : `validate` (ne modifie pas la structure)
- Logs : WARN/INFO
- Swagger : **désactivé par défaut** (configurable)
- Données de test : **non chargées**

### Déploiement sur Render

#### 1. Configuration initiale

1. Créez un service Web sur Render
2. Connectez votre repository GitHub
3. Render détecte automatiquement `render.yaml`

#### 2. Configuration des variables d'environnement

Dans le dashboard Render, allez dans **Environment** et ajoutez :

```bash
# Base de données (auto-générées si vous utilisez Render PostgreSQL)
DATABASE_URL=internal:postgresql://...
DATABASE_USERNAME=om_pay_db_user
DATABASE_PASSWORD=PhVa2xi4B20BdgrxeyEvxiJF13SCRBh6

# JWT - GÉNÉREZ UNE NOUVELLE CLÉ SÉCURISÉE !
JWT_SECRET=VotreNouvelleCléTrèsLongueEtAléatoirePourLaProduction2025
JWT_EXPIRATION=86400000

# Email Brevo - UTILISEZ VOTRE PROPRE CLÉ !
BREVO_API_KEY=xkeysib-votre_cle_api
MAIL_FROM_ADDRESS=noreply@votre-domaine.com
MAIL_FROM_NAME=OM Pay

# Swagger (optionnel)
SWAGGER_ENABLED=true
SWAGGER_SERVER_PROD_URL=https://votre-app.onrender.com

# Profil
SPRING_PROFILES_ACTIVE=prod
```

#### 3. Déploiement

```bash
# Render déploie automatiquement à chaque push sur main
git add .
git commit -m "feat: mise à jour Swagger et variables de déploiement"
git push origin main
```

#### 4. Vérification

Après le déploiement :
1. **Health check** : https://votre-app.onrender.com/actuator/health
2. **Swagger UI** : https://votre-app.onrender.com/swagger-ui.html (si activé)
3. **Test de login** : POST https://votre-app.onrender.com/api/auth/login

---

## 🔧 Optimisations JVM

Le `render.yaml` inclut des optimisations pour le plan gratuit de Render :

```yaml
startCommand: java -Xmx512m -Xms256m -jar target/om_pay-0.0.1-SNAPSHOT.jar
```

- `-Xmx512m` : Mémoire maximum de 512 MB
- `-Xms256m` : Mémoire initiale de 256 MB

Ces paramètres permettent à l'application de fonctionner dans les limites du plan gratuit (512 MB RAM).

---

## 📝 Checklist de déploiement

Avant de déployer en production, vérifiez :

- [ ] Changé `JWT_SECRET` avec une clé aléatoire sécurisée
- [ ] Configuré `BREVO_API_KEY` avec votre propre clé
- [ ] Configuré les credentials de la base de données
- [ ] Défini `SPRING_PROFILES_ACTIVE=prod`
- [ ] Décidé si Swagger doit être activé en production (`SWAGGER_ENABLED`)
- [ ] Testé le health check : `/actuator/health`
- [ ] Vérifié les logs dans le dashboard Render
- [ ] Testé l'authentification JWT
- [ ] Vérifié l'envoi d'emails

---

## 🆘 Dépannage

### Swagger ne s'affiche pas
- Vérifiez `SWAGGER_ENABLED=true`
- Vérifiez que vous êtes sur le profil `dev` ou que Swagger est activé en `prod`
- Accédez directement à `/swagger-ui.html` (pas `/swagger-ui`)

### Erreur de base de données
- Vérifiez `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- Vérifiez que la base PostgreSQL est bien créée sur Render

### Token JWT invalide
- Vérifiez que `JWT_SECRET` est bien configuré
- Vérifiez que le secret est identique partout (min 512 bits)

### Emails non envoyés
- Vérifiez `BREVO_API_KEY` dans le dashboard Brevo
- Vérifiez les logs de l'application
- Testez la clé API avec un client HTTP (Postman)

---

## 📚 Ressources

- [Springdoc OpenAPI Documentation](https://springdoc.org/)
- [Render Documentation](https://render.com/docs)
- [Brevo API Documentation](https://developers.brevo.com/)
- [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
