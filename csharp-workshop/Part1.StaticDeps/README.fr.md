# The Unit Question — Partie 1 : Isolation de méthode

> **Qu'est-ce qu'un "unit" dans un test unitaire ?**

## 🎯 À propos de cet atelier

Tout le monde parle de "tests unitaires", mais personne n'est d'accord sur ce qu'est une "unité".

- Est-ce une méthode ? Une classe ? Un module ?
- Faut-il tout mocker ? Rien mocker ? Un juste milieu ?
- Pourquoi certaines suites de tests cassent à chaque refactoring alors que d'autres survivent à des changements majeurs ?

**Cet atelier vous fait vivre différentes approches de test** à travers des exercices pratiques. Vous ressentirez les frictions de chaque stratégie et découvrirez laquelle vous convient.

4 parties, chacune avec une approche différente. Vous implémenterez des fonctionnalités et ressentirez par vous-même les avantages et inconvénients de chaque stratégie.

---

## 🛠️ Avant de commencer

### Prérequis

| Outil | Version | Vérification |
|-------|---------|--------------|
| **.NET SDK** | 8+ | `dotnet --version` |
| **Git** | n'importe | `git --version` |

NSubstitute et xUnit sont inclus (restaurés automatiquement).

### Installation

```bash
cd the-unit-question/csharp-workshop
dotnet test Part1.StaticDeps.Tests/
```

Vous devriez voir tous les tests passer (ou ignorés). Si c'est le cas, vous êtes prêt !

💡 Ouvrez `Part1.StaticDeps/` comme un **projet IDE séparé** pour éviter de modifier le mauvais `OrderService`.

---

## 🏋️ Exercice (~14 min)

`OrderService.PlaceOrder` ne supporte pas encore les codes de réduction. Votre mission : comprendre quels tests vous *devriez* écrire pour cette feature — et pourquoi ils sont difficiles à écrire dans cette architecture.

Ouvrez `OrderServiceTest.cs` — les méthodes d'exercice vous y attendent avec les instructions.

> ⚠️ **Note C#** : Dans cette partie, `OrderService` instancie toutes ses dépendances en ligne (`new StockService()`, `new PaymentService()`…). En Java, `mockConstruction()` de Mockito peut intercepter ces appels. En C#, NSubstitute n'a pas d'équivalent — il faudrait un outil commercial (JustMock, Typemock). Les tests d'exercice sont fournis comme stubs pour illustrer ce que vous *voudriez* implémenter.

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

**1.2.** Un `DiscountCodeService` est disponible dans la codebase. Câblez-le comme nouvelle dépendance (instancié en ligne, comme les autres). Il fournit `CheckDiscountCode(customerId, discountCode)` — retourne `true` si le code a **déjà** été utilisé par ce client.

**1.3.** Dans `PlaceOrder` : si un code de réduction est présent et **déjà utilisé**, rejetez la commande.

📝 Ouvrez `OrderServiceTest` → méthode `P1_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed`.

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

📝 Ouvrez `OrderServiceTest` → méthode `P1_OrderIsConfirmed_WhenDiscountCodeIsValid`.

---

### Étape 3 — Marquer le code comme utilisé après paiement

Objectif : après un paiement réussi, marquer le code comme utilisé.

**3.1.** Appelez `DiscountCodeService.MarkAsUsed(customerId, discountCode)` après un paiement réussi.

📝 Ouvrez `OrderServiceTest` → méthode `P1_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment`.

---

### 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on essaie d'ajouter une feature à cette codebase.

---

### 🤔 C'est quoi l'« unité » ici ?

Dans cette partie, l'unité est **une seule méthode**. Toutes les dépendances sont instanciées en ligne — pas d'injection, pas d'interfaces. Isoler `PlaceOrder` de ses collaborateurs nécessite d'intercepter des constructeurs, ce qui est trivial en Java (Mockito `mockConstruction`) mais demande un outil commercial en C# (JustMock, Typemock).

Les tests existants pour `IsValid` fonctionnent parce que cette méthode est `internal virtual` — NSubstitute peut créer un substitut partiel pour la tester en isolation.

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Partie 2](../Part2.LightMocks/README.fr.md)**
