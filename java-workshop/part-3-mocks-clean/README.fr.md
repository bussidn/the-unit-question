# The Unit Question — Part 3

## 🏋️ Exercice : Codes promo dans la création de commande

**⏱️ ~25 minutes**

La `PricingService` a été mise à jour pour supporter les codes promo. Elle expose maintenant deux méthodes :

- `calculateTotal(items)` — ancienne API, sans remise
- `calculateTotal(items, discountCode)` — nouvelle API avec code promo

Les tests de `PricingService` ont déjà été écrits — consultez `PricingServiceTest` pour comprendre le comportement attendu et vous inspirer du style.

`OrderService` n'est pas encore migré. C'est votre mission.

---

### 🎯 Votre mission

Migrez `OrderService` pour supporter les codes promo :

1. Ajoutez `PromoCodeService` comme dépendance
2. Changez la signature de `createOrder` pour accepter un `DiscountCode` optionnel
3. Si un code promo est fourni : utilisez `PromoCodeService.checkPromoCode` — si ça retourne `true`, le code est disponible et peut être appliqué ; sinon, rejetez. Calculez le prix avec remise, marquez-le comme utilisé après paiement
4. Écrivez `OrderServiceTest` en vous inspirant du style de `OrderCancellationServiceTest`

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

#### Scénario 3 : Stock insuffisant

```
ÉTANT DONNÉ une commande dont le stock est indisponible

QUAND le client passe la commande

ALORS la commande est rejetée avec le motif "Insufficient stock"
ET ni le pricing ni le paiement ne sont appelés
```

---

### 💡 Conseils

- Regardez `OrderCancellationServiceTest` pour le style : `@ExtendWith`, `@Mock`, `@InjectMocks`, helpers `given*` / `assert*`
- Le builder `OrderBuilder` est disponible dans `helper/`
- Consultez `PricingServiceTest` pour voir comment `PricingService` est testée

---

## ➡️ Suite

Passez à la **[Part 4](../part-4-behavior/README.fr.md)**