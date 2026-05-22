# The Unit Question — Part 4 : Tests comportementaux

## 🏋️ Exercice (~25 min)

Même exercice que la Part 3 — même feature, même code de production à migrer.

La différence est dans les tests.

---

## 🔄 Ce qui a changé depuis la Part 3

### Code de production

Les services implémentent maintenant des **interfaces** (ports). `OrderService` dépend d'abstractions, pas de classes concrètes. Cela permet une architecture ports-and-adapters où l'infrastructure est interchangeable.

### Style de test

En Part 3, on avait arrêté de mocker la logique pure (`PricingService`), mais on utilisait encore Mockito pour les services d'I/O. En Part 4, **Mockito a entièrement disparu**. Chaque dépendance est une vraie implémentation adossée à des structures en mémoire :

```java
orderService = new OrderService(
    orderRepository,                          // InMemoryOrderRepository
    new RepositoryStockService(stockRepo),    // vraie logique, données en mémoire
    new PricingService(),                     // réel — comme en Part 3
    new GatewayPaymentService(paymentGw),     // vraie logique, gateway en mémoire
    new GatewayShippingService(shippingGw)    // vraie logique, gateway en mémoire
);
```

Plus de `@Mock`. Plus de `when().thenReturn()`. Plus de `verify()`. Les tests décrivent des scénarios métier avec de vraies entrées et vérifient de vrais résultats.

Le `OrderBuilder` est toujours disponible — il est repris de la Part 3 et fonctionne de la même manière ici.

### 🤔 Discussion : c'est quoi l'« unité » ici ?

En Part 3, l'unité était encore **la classe** — on laissait tourner la logique pure mais on mockait les services d'I/O.

En Part 4, l'unité est **le cas d'usage** — `placeOrder` exécute toute la chaîne : validation → pricing → stock → paiement → expédition → persistence. Le test asserte qu'un scénario métier produit le résultat métier attendu.

> **Question à discuter :** Est-ce que ce sont encore des « tests unitaires » ? Si oui, c'est quoi l'« unité » ? Si non, c'est quoi ?

---

## Ce qui est fourni

Regardez le `OrderServiceTest` existant — il a été transformé en test comportemental. Il vous montre comment :
- Câbler les vraies implémentations à la main dans `@BeforeEach`
- Utiliser `InMemoryStockRepository` et les gateways en mémoire
- Asserter sur le comportement réel plutôt que sur les interactions de mocks

`InMemoryOrderRepository`, `InMemoryStockRepository` et `InMemoryDiscountCodeRepository` sont disponibles dans `helper/`.

`DiscountCodeService` est fourni avec ses tests. Il expose deux méthodes :

- **`checkDiscountCode(customerId, discountCode)`** — retourne `true` si le code est disponible pour ce client
- **`markAsUsed(customerId, discountCode)`** — marque le code comme utilisé pour ce client

`PricingService` a aussi été mis à jour avec une nouvelle surcharge : `calculateTotal(items, discountCode)` qui applique la réduction au prix.

---

## 🎯 Votre mission

`OrderService.placeOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

Regardez le `OrderServiceTest` existant pour le style et suivez les mêmes patterns.

---

### Étape 1 — Rejeter les codes de réduction déjà utilisés

Ajoutez un champ nullable `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `domain/`). La signature `placeOrder(Order)` reste inchangée.

Ajoutez `DiscountCodeService` comme dépendance. Dans `placeOrder` : si un code de réduction est présent et **non disponible**, rejetez la commande.

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

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour voir comment câbler les vraies implémentations
- `PricingService` n'a pas de dépendances — instanciez-le directement : `new PricingService()`

---

## ➡️ Fin de l'atelier 🎉

