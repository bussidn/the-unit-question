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

## 🛠️ Installation

```bash
cd java-workshop/part-4-behavior
./gradlew test
```

💡 Ouvrez cette partie comme un **projet IDE séparé** pour éviter d'éditer le mauvais `OrderService`.

---

## 🪂 Raccourci — code de production

Le code de production est identique à celui de la Part 3. **Envie de vous concentrer sur les tests ?** Fusionnez la branche de référence pour récupérer directement le code de prod :

```bash
git merge --no-edit origin/part-4-prod
```

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

## 🎯 Votre mission (~11 min)

`OrderService.placeOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

---

### Étape 1 — Rejeter les codes de réduction déjà utilisés

Objectif : rejeter une commande si le code de réduction a déjà été utilisé.

```
ÉTANT DONNÉ une commande avec le code de réduction SUMMER20
ET ce code a déjà été utilisé par ce client

QUAND le client valide la commande

ALORS la commande est rejetée avec la raison "Discount code already used"
ET aucun paiement n'est déclenché
```

**1.1.** Ajoutez un champ `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `domain/`). La signature `placeOrder(Order)` reste inchangée.

**1.2.** Ajoutez `DiscountCodeService` comme dépendance. Dans `placeOrder` : si un code de réduction est présent et **non disponible**, rejetez la commande.

📝 Ouvrez `OrderServiceTest` → méthode `p4_orderIsRejected_whenDiscountCodeIsAlreadyUsed`.
Les instructions et le scénario vous y attendent en commentaires.

---

### Étape 2 — Appliquer la réduction au prix

Objectif : appliquer la réduction au calcul du prix.

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET la commande est confirmée
```

**2.1.** Si le code de réduction est présent et disponible, utilisez `PricingService.calculateTotal(items, discountCode)` à la place de l'appel existant.

📝 Ouvrez `OrderServiceTest` → méthode `p4_orderIsConfirmed_whenDiscountCodeIsValid`.
Les instructions et le scénario vous y attendent en commentaires.

---

### Étape 3 — Marquer le code comme utilisé après paiement

Objectif : après un paiement réussi, marquer le code comme utilisé. On met à jour le test de l'étape 2 avec une nouvelle assertion :

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET la commande est confirmée
ET le code de réduction est marqué comme utilisé   ← NOUVEAU
```

**3.1.** Appelez `DiscountCodeService.markAsUsed(customerId, discountCode)` après un paiement réussi.

📝 Ouvrez `OrderServiceTest` → méthode `p4_discountCodeIsMarkedAsUsed_afterSuccessfulPayment`.
Les instructions vous y attendent en commentaires.

---

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour voir comment câbler les vraies implémentations
- `PricingService` n'a pas de dépendances — instanciez-le directement : `new PricingService()`

---

### 🤔 C'est quoi l'« unité » ici ?

Aucun mock. `placeOrder` exécute toute la chaîne — validation, pricing, stock, paiement, expédition, persistence — avec de vraies implémentations. L'unité est **le cas d'usage**. Est-ce que ce sont encore des « tests unitaires » ?

---

## ➡️ Fin de l'atelier 🎉

