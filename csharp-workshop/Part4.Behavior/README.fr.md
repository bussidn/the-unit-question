# The Unit Question — Partie 4 : Tests de comportement

## 🔄 Ce qui a changé depuis la Partie 3

Les services implémentent maintenant des **interfaces** (ports). `OrderService` dépend d'abstractions, pas de classes concrètes.

**NSubstitute a totalement disparu.** Chaque dépendance est une vraie implémentation soutenue par des données en mémoire :

```csharp
_orderService = new OrderService(
    _orderRepository,                              // InMemoryOrderRepository
    new RepositoryStockService(_stockRepository),  // vraie logique, données en mémoire
    new PricingService(),                          // réel — identique à la Partie 3
    new GatewayPaymentService(_paymentGateway),    // vraie logique, gateway en mémoire
    new GatewayShippingService(_shippingGateway)   // vraie logique, gateway en mémoire
);
```

Pas de `Substitute.For<>()`. Pas de `.Returns()`. Pas de `.Received()`. Les tests décrivent des scénarios métier avec de vraies entrées et vérifient de vrais résultats.

Regardez le `OrderServiceTest` existant (`Part4.Behavior.Tests/Service/`) avant de commencer.

---

## 🛠️ Installation

```bash
cd the-unit-question/csharp-workshop
dotnet test Part4.Behavior.Tests/
```

💡 Ouvrez `Part4.Behavior/` comme un **projet IDE séparé** pour éviter de modifier le mauvais `OrderService`.

---

## 🪂 Accès rapide — code de production

Le code de production est identique à la Partie 3. **Vous voulez vous concentrer sur les tests ?** Mergez la branche de référence pour obtenir le code de production directement :

```bash
git merge --no-edit origin/part-4-prod
```

---

## Ce qui est fourni

Regardez le `OrderServiceTest` existant — il a été transformé en test de comportement. Il vous montre comment :
- Câbler de vraies implémentations à la main dans le constructeur
- Utiliser `InMemoryStockRepository` et les gateways en mémoire
- Asserter sur le comportement réel plutôt que sur les interactions avec des mocks

`InMemoryOrderRepository`, `InMemoryStockRepository`, `InMemoryDiscountCodeRepository` et les gateways en mémoire sont disponibles dans `Helper/`.

`DiscountCodeService` est fourni avec ses tests. Il expose deux méthodes :

- **`CheckDiscountCode(customerId, discountCode)`** — retourne `true` si le code a **déjà** été utilisé par ce client
- **`MarkAsUsed(customerId, discountCode)`** — marque le code comme utilisé pour ce client

`PricingService` a aussi été mis à jour avec une nouvelle surcharge : `CalculateTotal(items, discountCode)` qui applique la réduction au prix.

---

## 🎯 Votre mission (~11 min)

`OrderService.PlaceOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

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

**1.1.** Ajoutez un champ `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `Domain/`). La signature `PlaceOrder(Order)` reste inchangée.

**1.2.** Ajoutez `DiscountCodeService` comme dépendance. Dans `PlaceOrder` : si un code de réduction est présent et **déjà utilisé**, rejetez la commande.

📝 Ouvrez `OrderServiceTest` → méthode `P4_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed`.
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

**2.1.** Si le code de réduction est présent et disponible, utilisez `PricingService.CalculateTotal(items, discountCode)` à la place de l'appel existant.

📝 Ouvrez `OrderServiceTest` → méthode `P4_OrderIsConfirmed_WhenDiscountCodeIsValid`.
Les instructions et le scénario vous y attendent en commentaires.

---

### Étape 3 — Marquer le code comme utilisé après paiement

Objectif : après un paiement réussi, marquer le code comme utilisé. Mettez à jour le test de l'étape 2 avec une nouvelle assertion :

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET la commande est confirmée
ET le code de réduction est marqué comme utilisé   ← NOUVEAU
```

**3.1.** Appelez `DiscountCodeService.MarkAsUsed(customerId, discountCode)` après un paiement réussi.

📝 Ouvrez `OrderServiceTest` → méthode `P4_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment`.
Les instructions vous y attendent en commentaires.

---

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour voir comment câbler de vraies implémentations
- Utilisez `InMemoryDiscountCodeRepository` (dans `Helper/`) pour configurer l'état des codes de réduction
- `PricingService` n'a pas de dépendances — instanciez-le directement : `new PricingService()`

---

### 🤔 C'est quoi l'« unité » ici ?

Aucun substitut. `PlaceOrder` exécute toute la chaîne — validation, tarification, stock, paiement, expédition, persistance — avec de vraies implémentations. L'unité est **le cas d'usage**. Est-ce encore des "tests unitaires" ?

---

## ➡️ Fin de l'atelier 🎉
