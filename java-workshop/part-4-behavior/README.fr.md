# The Unit Question — Part 4

## 🏋️ Exercice : Codes promo dans la création de commande

**⏱️ ~25 minutes**

Même exercice que la Part 3 — même feature, même code de production à migrer.

La différence est dans les tests.

---

## Ce qui change

Dans les étapes précédentes, les collaborateurs de `OrderService` étaient tous mockés. Chaque mock encodait une hypothèse sur ce que ferait `PricingService`, `StockService`, etc. silencieusement.

En Part 4, on supprime ces hypothèses. Les tests utilisent **les vraies implémentations** des services internes. Seuls les **gateways externes** restent mockés — ils représentent des systèmes tiers qu'on ne contrôle pas.

Pour les repositories, on utilise des **fakes** : des implémentations en mémoire simples, sans base de données.

---

## Ce qui est fourni

Regardez `OrderCancellationServiceTest` — il a été transformé en test comportemental. Il vous montre comment :
- Câbler les vraies implémentations à la main dans `@BeforeEach`
- Utiliser `InMemoryOrderRepository` et `InMemoryStockRepository`
- Garder `@Mock` uniquement sur `PaymentGateway` et `ShippingGateway`

`InMemoryOrderRepository`, `InMemoryStockRepository` et `InMemoryDiscountCodeRepository` sont disponibles dans `helper/`.

`DiscountCodeService` est fourni avec ses tests — utilisez `DiscountCodeService.checkDiscountCode(customerId, discountCode)` : s'il retourne `true`, le code est disponible.

---

## 🎯 Votre mission

1. Migrez `OrderService` pour supporter les codes promo — ajoutez `DiscountCodeService` comme dépendance, utilisez `checkDiscountCode` pour valider, `markAsUsed` après paiement
2. Écrivez `OrderServiceTest` en style comportemental

Lancez les tests : `./gradlew test`

---

### Scénarios à implémenter dans `OrderServiceTest`

#### Scénario 1 : Création avec code promo valide

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code promo SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client
ET un stock suffisant

QUAND le client passe la commande

ALORS le paiement est traité pour 105.60€
ET le code promo est marqué comme utilisé
ET la commande est confirmée
```

#### Scénario 2 : Code promo déjà utilisé

```
ÉTANT DONNÉ une commande avec le code promo SUMMER20
ET ce code a déjà été utilisé par ce client

QUAND le client passe la commande

ALORS la commande est rejetée avec le motif "Discount code already used"
ET aucun paiement n'est déclenché
```

#### Scénario 3 : Stock insuffisant

```
ÉTANT DONNÉ une commande dont le stock est indisponible

QUAND le client passe la commande

ALORS la commande est rejetée avec le motif "Insufficient stock"
ET ni le pricing ni le paiement ne sont appelés
```

---

### 💡 Conseils

- Regardez `OrderCancellationServiceTest` pour voir comment câbler les vraies implémentations
- `PricingService` n'a pas de dépendances — instanciez-le directement : `new PricingService()`
- Gardez `@Mock` uniquement sur `PaymentGateway` et `ShippingGateway`
- Consultez `PricingServiceTest` pour comprendre le comportement de `PricingService`

---

## ➡️ Fin de l'atelier 🎉

