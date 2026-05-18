# The Unit Question — Part 4 : Tests comportementaux

## 🏋️ Exercice : Codes promo dans la création de commande

**⏱️ ~25 minutes**

Même exercice que la Part 3 — même feature, même code de production à migrer.

La différence est dans les tests.

---

## 🔄 Ce qui a changé depuis la Part 3

Dans les parties précédentes, tous les collaborateurs de `OrderService` étaient mockés. Chaque mock encodait silencieusement une hypothèse sur ce que feraient `PricingService`, `StockService`, etc.

En Part 4, on supprime ces hypothèses. Les tests utilisent **les vraies implémentations** des services internes. Seuls les **gateways externes** restent mockés — ils représentent des systèmes tiers qu'on ne contrôle pas.

Pour les repositories, on utilise des **fakes** : des implémentations en mémoire simples, sans base de données.

Le `OrderBuilder` est toujours disponible — il est repris de la Part 3 et fonctionne de la même manière ici.

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

`OrderService.placeOrder` doit supporter les codes promo.

- Ajouter un champ nullable `discountCode` au record `Order` (l'enum `DiscountCode` est déjà fourni dans `domain/`)
- Ajouter `DiscountCodeService` comme dépendance, utiliser `checkDiscountCode` pour valider, `markAsUsed` après paiement
- Écrire `OrderServiceTest` en style comportemental

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
---

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour voir comment câbler les vraies implémentations
- `PricingService` n'a pas de dépendances — instanciez-le directement : `new PricingService()`

---

## ➡️ Fin de l'atelier 🎉

