# The Unit Question — Partie 3 : Mocks propres

## 🔄 Ce qui a changé depuis la Partie 2

Même code de production, mêmes dépendances. Le changement est dans **le design des tests** :

- Un `OrderBuilder` (dans `Helper/`) rend les données de test plus lisibles.
- Le setup répété est factorisé dans des **helpers given** (`GivenPaymentSucceedsFor`, `GivenReservationSucceedsFor`, etc.).
- `PricingService` n'est plus substitué — c'est du calcul pur, donc les tests assertent sur des valeurs réelles calculées.

Regardez le `OrderServiceTest` existant (`Part3.MocksClean.Tests/Service/`) avant de commencer.

---

## 🛠️ Installation

```bash
cd the-unit-question/csharp-workshop
dotnet test Part3.MocksClean.Tests/
```

💡 Ouvrez `Part3.MocksClean/` comme un **projet IDE séparé** pour éviter de modifier le mauvais `OrderService`.

---

## 🪂 Accès rapide — code de production

Le code de production est identique à la Partie 2. **Vous voulez vous concentrer sur les tests ?** Mergez la branche de référence pour obtenir le code de production directement :

```bash
git merge --no-edit origin/part-3-prod
```

---

## 🏋️ Exercice (~13 min)

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

**1.2.** Un `DiscountCodeService` est disponible dans la codebase. Ajoutez `IDiscountCodeService` comme dépendance. Il fournit `CheckDiscountCode(customerId, discountCode)` — retourne `true` si le code a **déjà** été utilisé par ce client.

**1.3.** Dans `PlaceOrder` : si un code de réduction est présent et **déjà utilisé**, rejetez la commande.

📝 Ouvrez `OrderServiceTest` → méthode `P3_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed`.
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

📝 Ouvrez `OrderServiceTest` → méthode `P3_OrderIsConfirmed_WhenDiscountCodeIsValid`.
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

📝 Ouvrez `OrderServiceTest` → méthode `P3_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment`.
Les instructions vous y attendent en commentaires.

---

### 💡 Conseils

- Utilisez `OrderBuilder` (dans `Helper/`) pour construire les commandes de test — ça garde le bloc GIVEN centré sur ce qui compte
- Inspirez-vous des helpers given existants pour en créer de nouveaux

---

### 🤔 C'est quoi l'« unité » ici ?

Même code de production que la Partie 2, mais design de test différent. Les helpers given absorbent les changements d'API, et `PricingService` s'exécute pour de vrai. L'unité est toujours la classe — mais la frontière de ce qu'on substitue a bougé.

---

## ➡️ Suite

Passez à la **[Partie 4](../Part4.Behavior/README.fr.md)**
