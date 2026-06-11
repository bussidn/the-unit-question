# The Unit Question — Partie 2 : Mocks légers

## 🔄 Ce qui a changé depuis la Partie 1

Les dépendances sont maintenant **injectées via le constructeur** au lieu d'être instanciées en ligne. Les tests peuvent utiliser NSubstitute directement et passer des substituts dans le constructeur — plus besoin d'outils commerciaux.

`IsValid()` s'exécute aussi pour de vrai maintenant — plus de contournements avec `internal virtual`.

La logique de production est identique à la Partie 1 ; seul le câblage a changé. Regardez le `OrderServiceTest` existant (`Part2.LightMocks.Tests/Service/`) avant de commencer.

---

## 🛠️ Installation

```bash
cd the-unit-question/csharp-workshop
dotnet test Part2.LightMocks.Tests/
```

💡 Ouvrez `Part2.LightMocks/` comme un **projet IDE séparé** pour éviter de modifier le mauvais `OrderService`.

---

## 🪂 Accès rapide — code de production

Vous referez les modifications de production de la Partie 1 ici (c'est rapide). **Vous voulez vous concentrer sur les tests ?** Mergez la branche de référence pour obtenir le code de production directement :

```bash
git merge --no-edit origin/part-2-prod
```

---

## 🏋️ Exercice (~15 min)

`OrderService.PlaceOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

---

### Étape 1 — Rejeter les codes de réduction déjà utilisés

Objectif : rejeter les commandes dont le code de réduction a déjà été utilisé.

```
ÉTANT DONNÉ une commande avec le code de réduction SUMMER20
ET ce code a déjà été utilisé par ce client

QUAND le client valide la commande

ALORS la commande est rejetée avec la raison "Discount code already used"
ET aucun paiement n'est déclenché
```

**1.1.** Ajoutez un champ `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `Domain/`). La signature `PlaceOrder(Order)` reste inchangée.

**1.2.** Un `DiscountCodeService` est disponible dans la codebase. Ajoutez `IDiscountCodeService` comme dépendance de constructeur. Il fournit `CheckDiscountCode(customerId, discountCode)` — retourne `true` si le code a **déjà** été utilisé par ce client.

**1.3.** Dans `PlaceOrder` : si un code de réduction est présent et **déjà utilisé**, rejetez la commande.

📝 Ouvrez `OrderServiceTest` → méthode `P2_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed`.
Les instructions et le scénario vous y attendent en commentaires.

---

### Étape 2 — Appliquer la réduction au prix

Objectif : appliquer la réduction au prix quand le code est valide.

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET la commande est confirmée
```

**2.1.** Si le code de réduction est présent et disponible, utilisez `PricingService.CalculateTotal(items, discountCode)` à la place de l'appel existant.

📝 Ouvrez `OrderServiceTest` → méthode `P2_OrderIsConfirmed_WhenDiscountCodeIsValid`.
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

📝 Ouvrez `OrderServiceTest` → méthode `P2_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment`.
Les instructions vous y attendent en commentaires.

---

### 🤔 C'est quoi l'« unité » ici ?

Dans cette partie, l'unité est **la classe**. `OrderService` s'exécute entièrement pour de vrai, et chaque dépendance externe est substituée. On ne stubber plus les méthodes internes comme `IsValid`.

---

## ➡️ Suite

Passez à la **[Partie 3](../Part3.MocksClean/README.fr.md)**
