# The Unit Question — Part 2

## 🏋️ Exercice : Codes promo dans la création de commande

**⏱️ ~25 minutes**

La `PricingService` a été mise à jour pour supporter les codes promo. Elle expose maintenant deux méthodes :

- `calculateTotal(items)` — ancienne API, sans remise
- `calculateTotal(items, discountCode)` — nouvelle API avec code promo

Les tests de `PricingService` ont déjà été écrits — consultez `PricingServiceTest` pour comprendre comment le pricing calcule les totaux avec les remises.

`OrderService` n'est pas encore migré. C'est votre mission.

---

### 🎯 Votre mission

Migrez `OrderService` pour supporter les codes promo :

1. Ajoutez `DiscountCodeService` comme dépendance
2. Changez la signature de `createOrder` pour accepter un `DiscountCode` optionnel
3. Si un code promo est fourni : utilisez `DiscountCodeService.checkDiscountCode` — si ça retourne `true`, le code est disponible et peut être appliqué ; sinon, rejetez. Calculez le prix avec remise, marquez-le comme utilisé après paiement
4. Ajoutez vos nouveaux scénarios de test dans le `OrderServiceTest` existant — suivez le style déjà en place

Lancez les tests : `./gradlew test`

---

### Scénarios à implémenter dans `OrderServiceTest`

#### Scénario 1 : Création sans code promo

```
ÉTANT DONNÉ une commande de 2 articles à 10€ chacun
ET aucun code promo

QUAND le client passe la commande

ALORS le paiement est traité pour le montant calculé par PricingService
ET la commande est confirmée avec une expédition
```

#### Scénario 2 : Création avec code promo valide

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code promo SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client passe la commande

ALORS le paiement est traité pour 105.60€
ET le code promo est marqué comme utilisé
ET la commande est confirmée
```

#### Scénario 3 : Code promo déjà utilisé

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
- Le builder `OrderBuilder` est disponible dans `helper/`
- Consultez `PricingServiceTest` pour comprendre comment le pricing calcule les totaux avec les remises

---

## ➡️ Suite

Passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**