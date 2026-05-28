# The Unit Question — Part 4 : Tests comportementaux

## 🔄 Ce qui a changé depuis la Part 3

Les services implémentent maintenant des **interfaces** (ports). `OrderService` dépend d'abstractions, pas de classes concrètes.

**Mockito a entièrement disparu.** Chaque dépendance est une vraie implémentation adossée à des structures en mémoire :

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

Jetez un œil au `OrderServiceTest` existant (`src/test/java/service/`) avant de commencer.

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

---

### Étape 1 — Rejeter les codes de réduction déjà utilisés

**1.1.** Ajoutez un champ nullable `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `domain/`). La signature `placeOrder(Order)` reste inchangée.

**1.2.** Ajoutez `DiscountCodeService` comme dépendance. Dans `placeOrder` : si un code de réduction est présent et **non disponible**, rejetez la commande.

**Test à écrire :**

```
ÉTANT DONNÉ une commande avec le code de réduction SUMMER20
ET ce code a déjà été utilisé par ce client

QUAND le client valide la commande

ALORS la commande est rejetée avec la raison "Discount code already used"
ET aucun paiement n'est déclenché
```

> 📝 **1.3.** Implémentez dans `OrderServiceTest` → cherchez **Step 1**

---

### Étape 2 — Appliquer la réduction au prix

**2.1.** Si le code de réduction est présent et disponible, utilisez `PricingService.calculateTotal(items, discountCode)` à la place de l'appel existant.

**Test à écrire :**

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET la commande est confirmée
```

> 📝 **2.2.** Implémentez dans `OrderServiceTest` → cherchez **Step 2**

---

### Étape 3 — Marquer le code comme utilisé après paiement

**3.1.** Après un paiement réussi, appelez `DiscountCodeService.markAsUsed(customerId, discountCode)`.

**3.2.** **Mettez à jour votre test précédent** pour asserter que le code est marqué comme utilisé.

> 📝 **3.3.** Implémentez dans `OrderServiceTest` → cherchez **Step 3**

---

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour voir comment câbler les vraies implémentations
- `PricingService` n'a pas de dépendances — instanciez-le directement : `new PricingService()`

---

### 🤔 C'est quoi l'« unité » ici ?

Aucun mock. `placeOrder` exécute toute la chaîne — validation, pricing, stock, paiement, expédition, persistence — avec de vraies implémentations. L'unité est **le cas d'usage**. Est-ce que ce sont encore des « tests unitaires » ?

---

## ➡️ Fin de l'atelier 🎉

