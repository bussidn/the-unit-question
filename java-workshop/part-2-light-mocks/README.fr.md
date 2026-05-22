# The Unit Question — Part 2 : Injection par constructeur

## 🔄 Ce qui a changé depuis la Part 1

### Code de production

En Part 1, `OrderService` instanciait tous ses collaborateurs (`StockService`, `PricingService`, etc.) directement dans `placeOrder`. Pour tester ce code, il fallait des **hacks de type PowerMock** : `mockConstruction()` pour intercepter les appels `new SomeService()`, et `mockStatic()` pour stuber les méthodes statiques.

En Part 2, ces mêmes services sont maintenant **injectés via le constructeur** :

```java
public OrderService(
    StockService stockService,
    PricingService pricingService,
    PaymentService paymentService,
    ShippingService shippingService
) { ... }
```

Ce seul changement structurel suffit à faire disparaître entièrement les hacks. Les tests peuvent maintenant utiliser de simples appels **`mock()`** et passer les fakes directement dans le constructeur.

### Style de test

Plus de `mockConstruction()`. Plus de `mockStatic()`. Plus de try-with-resources englobant chaque test. La méthode `isValid()` s'exécute aussi pour de vrai maintenant — plus besoin de `whenPrivate` pour la stuber.

La logique métier dans `placeOrder` fait **exactement la même chose** qu'en Part 1 — la seule différence est d'où viennent les dépendances. Jetez un œil au `OrderServiceTest` existant pour voir à quel point le style de test est devenu plus simple avant de commencer l'exercice.

### 🤔 Discussion : c'est quoi l'« unité » ici ?

En Part 1, l'unité était **une seule méthode** — on mockait même les autres méthodes de la classe sous test (`isValid`).

En Part 2, l'unité est **la classe** — `OrderService` s'exécute entièrement pour de vrai, mais toutes les dépendances externes sont mockées.

> **Question à discuter :** Qu'est-ce qu'on a gagné en passant de l'isolation par méthode à l'isolation par classe ? Quels problèmes de la Part 1 disparaissent ? Est-ce que de nouveaux apparaissent ?

---

## 🏋️ Exercice (~25 min)

`OrderService.placeOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

Regardez le `OrderServiceTest` existant pour le style et suivez les mêmes patterns.

---

### Étape 1 — Rejeter les codes de réduction déjà utilisés

Ajoutez un champ nullable `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `domain/`). La signature `placeOrder(Order)` reste inchangée.

Un `DiscountCodeService` est disponible dans la codebase. Ajoutez-le comme dépendance du constructeur. Il fournit `checkDiscountCode(customerId, discountCode)` — retourne `true` si le code est disponible pour ce client.

Dans `placeOrder` : si un code de réduction est présent et **non disponible**, rejetez la commande.

**Test à écrire :**

```
ÉTANT DONNÉ une commande avec le code de réduction SUMMER20
ET ce code a déjà été utilisé par ce client

QUAND le client valide la commande

ALORS la commande est rejetée avec la raison "Discount code already used"
ET aucun paiement n'est déclenché
```

Lancez : `./gradlew test` ✅

---

### Étape 2 — Appliquer la réduction au prix

Si le code de réduction est présent et disponible, utilisez `PricingService.calculateTotal(items, discountCode)` à la place de l'appel existant.

**Test à écrire :**

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET la commande est confirmée
```

Lancez : `./gradlew test` ✅

---

### Étape 3 — Marquer le code comme utilisé après paiement

Après un paiement réussi, appelez `DiscountCodeService.markAsUsed(customerId, discountCode)`.

**Mettez à jour votre test précédent** pour asserter que le code est marqué comme utilisé.

Lancez : `./gradlew test` ✅

---

## ➡️ Suite

Passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**