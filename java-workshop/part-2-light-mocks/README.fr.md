# The Unit Question — Part 2 : Injection par constructeur

## 🔄 Ce qui a changé depuis la Part 1

Les dépendances sont maintenant **injectées via le constructeur** au lieu d'être instanciées en dur. Les tests utilisent de simples appels `mock()` passés directement au constructeur — plus de `mockConstruction`, `mockStatic`, ni de try-with-resources autour de chaque test.

`isValid()` s'exécute aussi pour de vrai maintenant — plus besoin de `whenPrivate` pour la stuber.

La logique métier est la même qu'en Part 1 ; seul le câblage a changé. Jetez un œil au `OrderServiceTest` existant (`src/test/java/service/`) avant de commencer.

---

## 🏋️ Exercice (~25 min)

`OrderService.placeOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

---@@

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

> 📝 Implémentez dans `OrderServiceTest` → cherchez **Step 1**

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

> 📝 Implémentez dans `OrderServiceTest` → cherchez **Step 2**

Lancez : `./gradlew test` ✅

---

### Étape 3 — Marquer le code comme utilisé après paiement

Après un paiement réussi, appelez `DiscountCodeService.markAsUsed(customerId, discountCode)`.

**Mettez à jour votre test précédent** pour asserter que le code est marqué comme utilisé.

> 📝 Implémentez dans `OrderServiceTest` → cherchez **Step 3**

Lancez : `./gradlew test` ✅

---

### 🤔 C'est quoi l'« unité » ici ?

Dans cette partie, l'unité est **la classe**. `OrderService` s'exécute entièrement pour de vrai, et toutes les dépendances externes sont mockées. On ne stubbe plus les méthodes internes comme `isValid`.

---

## ➡️ Suite

Passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**
