# The Unit Question — Part 2

## 🏋️ Exercice : Code Promo

**⏱️ ~20-25 minutes**

On veut permettre aux clients d'appliquer un code promo à leur commande.

---

## 💡 Ce qui existe déjà

- `PricingService` gère déjà les taxes (20%) et les frais de livraison (5€ si sous-total < 100€)
- L'enum [`DiscountCode`](src/main/java/domain/DiscountCode.java) liste les codes disponibles avec leur pourcentage de réduction
- [`PromoCodeService`](src/main/java/service/PromoCodeService.java) gère la validation des codes promo — `checkPromoCode(customerId, discountCode)` retourne `true` si le code est disponible, et `markAsUsed` enregistre l'utilisation

---

## 🔧 Ce qu'il faut implémenter

### Dans `PricingService`

Ajoutez une surcharge de `calculateTotal` qui accepte un `DiscountCode` en plus de la liste d'articles.

La réduction s'applique **sur le sous-total**, avant le calcul des taxes et des frais de livraison.

### Dans `OrderService`

Modifiez `createOrder` pour accepter un `DiscountCode` optionnel et :
1. **Vérifier** via `PromoCodeService.checkPromoCode` si le code est disponible — si `checkPromoCode` retourne `true`, le code peut être appliqué ; sinon, rejeter la commande
2. **Enregistrer** l'utilisation du code (via `markAsUsed`) après confirmation de la commande

`PromoCodeService` doit être injecté dans `OrderService`.

---

## ✅ Tests à mettre à jour

### `PricingServiceTest`

#### Scénario A — Réduction appliquée, livraison ajoutée

```
ÉTANT DONNÉ des articles pour 100€
ET un code SUMMER20 (20% de réduction)

ALORS le sous-total après réduction est 80€
ET les taxes (20%) s'appliquent sur 80€ → +16€
ET les frais de livraison s'appliquent (80€ < seuil 100€) → +5€
ET le total est 101€
```

#### Scénario B — Réduction appliquée, livraison offerte

```
ÉTANT DONNÉ des articles pour 150€
ET un code SUMMER20 (20% de réduction)

ALORS le sous-total après réduction est 120€
ET les taxes (20%) s'appliquent sur 120€ → +24€
ET les frais de livraison sont offerts (120€ ≥ seuil 100€)
ET le total est 144€
```

---

### `OrderServiceTest`

#### Scénario C — Code valide, commande acceptée

```
ÉTANT DONNÉ une commande avec un code promo
ET le client n'a pas encore utilisé ce code

QUAND la commande est créée

ALORS la commande est confirmée
ET le code est marqué comme utilisé pour ce client
```

#### Scénario D — Code déjà utilisé, commande rejetée

```
ÉTANT DONNÉ une commande avec un code promo
ET le client a déjà utilisé ce code

QUAND la commande est créée

ALORS la commande est rejetée avec "Code already used"
ET aucune autre étape n'est exécutée (stock, paiement, livraison)
```

---

## Votre mission

1. Regardez le code existant dans `PricingService` et `OrderService`
2. Implémentez la feature
3. Mettez à jour les tests dans `PricingServiceTest` et `OrderServiceTest`
4. Lancez les tests

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**

