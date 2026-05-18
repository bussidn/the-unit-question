# The Unit Question — Part 2 : Injection par constructeur

## 🔄 Ce qui a changé depuis la Part 1

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

Ce seul changement structurel suffit à faire disparaître entièrement les hacks. Les tests peuvent maintenant utiliser de simples appels **`mock()`** et passer les fakes directement dans le constructeur :

```java
@BeforeEach
void setUp() {
    stockService    = mock(StockService.class);
    pricingService  = mock(PricingService.class);
    paymentService  = mock(PaymentService.class);
    shippingService = mock(ShippingService.class);
    orderService    = new OrderService(stockService, pricingService, paymentService, shippingService);
}
```

Plus de `mockConstruction()`. Plus de `mockStatic()`. Plus de try-with-resources englobant chaque test.

La logique métier dans `placeOrder` fait **exactement la même chose** qu'en Part 1 — la seule différence est d'où viennent les dépendances. Jetez un œil au `OrderServiceTest` existant pour voir à quel point le style de test est devenu plus simple avant de commencer l'exercice.

---

## 🏋️ Exercice : Codes promo dans la création de commande

**⏱️ ~25 minutes**

Un `DiscountCodeService` a été ajouté à la codebase. Il expose deux méthodes :

- **`checkDiscountCode(customerId, discountCode)`** — retourne `true` si le code est disponible pour ce client
- **`markAsUsed(customerId, discountCode)`** — marque le code comme utilisé pour ce client

`PricingService` a aussi été mis à jour avec une nouvelle surcharge : `calculateTotal(items, discountCode)` qui applique la réduction au prix.

`OrderService` n'est pas encore migré. C'est votre mission.

---

### 🎯 Votre mission

`OrderService.placeOrder` doit supporter les codes promo.

- Ajouter `DiscountCodeService` comme dépendance du constructeur
- Ajouter un champ `discountCode` au record `Order` — il doit être nullable (l'enum `DiscountCode` est déjà fourni dans `domain/`). La signature `placeOrder(Order)` reste inchangée
- Si un code promo est fourni : utiliser `DiscountCodeService.checkDiscountCode` — si ça retourne `true`, le code est disponible et peut être appliqué ; sinon, rejetez. Calculez le prix avec remise, marquez-le comme utilisé après paiement
- Ajoutez vos nouveaux scénarios de test dans le `OrderServiceTest` existant — suivez le style déjà en place

Lancez les tests : `./gradlew test`

---

### Scénarios à implémenter dans `OrderServiceTest`

#### Scénario 1 : Création avec code promo valide

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code promo SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

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

---

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour le style et suivez les mêmes patterns

---

## ➡️ Suite

Passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**