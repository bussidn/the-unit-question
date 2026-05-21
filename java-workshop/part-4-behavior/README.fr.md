# The Unit Question — Part 4 : Tests comportementaux

## 🏋️ Exercice (~25 min)

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

