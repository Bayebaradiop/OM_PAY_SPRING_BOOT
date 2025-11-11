# 📋 Guide d'utilisation des Projections

## ✅ Projections créées

### **1. TransactionProjection** (Projection complète)
Interface pour récupérer toutes les informations d'une transaction avec relations imbriquées.

**Champs disponibles :**
- `getId()`, `getReference()`, `getTypeTransaction()`
- `getMontant()`, `getFrais()`, `getMontantTotal()`
- `getStatut()`, `getDateTransaction()`
- `getCompteExpediteur()` → Projection imbriquée du compte
- `getCompteDestinataire()` → Projection imbriquée du compte
- `getDistributeur()` → Projection imbriquée (nullable)
- `getMarchand()` → Projection imbriquée (nullable)

**Utilisation :**
```java
List<TransactionProjection> transactions = transactionRepository.findProjectionByCompteId(compteId);
```

---

### **2. TransactionHistoriqueProjection** (Projection historique)
Interface optimisée pour l'affichage d'historique avec informations aplaties.

**Champs disponibles :**
- `getReference()`, `getTypeTransaction()`
- `getMontant()`, `getFrais()`, `getMontantTotal()`
- `getStatut()`, `getDateTransaction()`
- `getNumeroCompteExpediteur()`, `getNumeroCompteDestinataire()`
- `getNomExpediteur()`, `getTelephoneExpediteur()`
- `getNomDestinataire()`, `getTelephoneDestinataire()`

**Utilisation :**
```java
List<TransactionHistoriqueProjection> historique = transactionRepository.findHistoriqueProjection(
    compteId, 
    dateDebut, 
    dateFin
);
```

---

### **3. TransactionSummaryProjection** (Projection résumé)
Interface minimale pour listes légères et statistiques.

**Champs disponibles :**
- `getId()`, `getReference()`, `getTypeTransaction()`
- `getMontant()`, `getStatut()`, `getDateTransaction()`

**Utilisation :**
```java
// Par compte
List<TransactionSummaryProjection> summary = transactionRepository.findSummaryByCompteId(compteId);

// Par distributeur
List<TransactionSummaryProjection> operations = transactionRepository.findSummaryByDistributeurId(distributeurId);

// Par type
List<TransactionSummaryProjection> transferts = transactionRepository.findSummaryByType(TypeTransaction.TRANSFERT);
```

---

## 🚀 Méthodes Repository disponibles

| Méthode | Type retour | Description |
|---------|-------------|-------------|
| `findProjectionByCompteId(compteId)` | `List<TransactionProjection>` | Toutes transactions avec relations |
| `findHistoriqueProjection(compteId, dateDebut, dateFin)` | `List<TransactionHistoriqueProjection>` | Historique optimisé |
| `findSummaryByCompteId(compteId)` | `List<TransactionSummaryProjection>` | Résumé par compte |
| `findSummaryByDistributeurId(distributeurId)` | `List<TransactionSummaryProjection>` | Résumé par distributeur |
| `findSummaryByType(type)` | `List<TransactionSummaryProjection>` | Résumé par type |

---

## 📊 Quand utiliser quelle projection ?

| Besoin | Projection recommandée | Raison |
|--------|------------------------|---------|
| **Détails transaction complète** | TransactionProjection | Inclut toutes les relations |
| **Affichage historique** | TransactionHistoriqueProjection | Optimisé pour UI, données aplaties |
| **Liste rapide** | TransactionSummaryProjection | Minimal, très performant |
| **Statistiques** | TransactionSummaryProjection | Léger pour calculs |
| **Export PDF/Excel** | TransactionHistoriqueProjection | Toutes infos nécessaires |

---

## 💡 Exemples d'utilisation dans le Service

```java
@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    // Exemple 1 : Historique pour UI
    public List<TransactionHistoriqueProjection> getHistorique(Long compteId, LocalDateTime debut, LocalDateTime fin) {
        return transactionRepository.findHistoriqueProjection(compteId, debut, fin);
    }
    
    // Exemple 2 : Statistiques rapides
    public Double calculerTotalTransferts(Long compteId) {
        return transactionRepository.findSummaryByCompteId(compteId)
            .stream()
            .filter(t -> t.getTypeTransaction() == TypeTransaction.TRANSFERT)
            .mapToDouble(TransactionSummaryProjection::getMontant)
            .sum();
    }
    
    // Exemple 3 : Détails complets
    public List<TransactionProjection> getTransactionsCompletes(Long compteId) {
        return transactionRepository.findProjectionByCompteId(compteId);
    }
}
```

---

## ⚠️ Notes importantes

1. **Performance** : Les projections sont plus rapides que charger l'entité complète
2. **Lazy Loading** : Les interfaces projections supportent le lazy loading
3. **JSON** : Les projections se sérialisent automatiquement en JSON
4. **Immuabilité** : Les projections sont read-only (pas de setters)

---

## 🔧 Compilation

Si erreur de compilation "cannot find symbol":
```bash
# Nettoyer et recompiler
./mvnw clean compile -DskipTests

# Si ça persiste, compiler en deux étapes
./mvnw clean
./mvnw compile -DskipTests
```
