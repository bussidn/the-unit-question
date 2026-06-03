# The Unit Question — Part 3 : Clean mocks

## 🔄 Ce qui a changé depuis la Part 2

Même code de production, mêmes dépendances. Le changement est dans le **design des tests** :

- `@ExtendWith(MockitoExtension.class)` et annotations `@Mock` remplacent les appels `mock()` manuels.
- Un `OrderBuilder` (dans `helper/`) rend les données de test plus lisibles.
- Les setups récurrents sont factorisés dans des **given helpers** (`givenReservationSucceedsFor`, `givenPaymentSucceedsFor`, etc.).
- `PricingService` n'est plus mocké — c'est du calcul pur, donc les tests assertent sur de vraies valeurs calculées.

Jetez un œil au `OrderServiceTest` existant (`src/test/java/service/`) avant de commencer.

---

## 🛠️ Installation

```bash
cd java-workshop/part-3-mocks-clean
./gradlew test
```

💡 Ouvrez cette partie comme un **projet IDE séparé** pour éviter d'éditer le mauvais `OrderService`.

---

## 🪂 Raccourci — code de production

Le code de production est identique à celui de la Part 2. **Envie de vous concentrer sur les tests ?** Fusionnez la branche de référence pour récupérer directement le code de prod :

```bash
git merge --no-edit origin/part-3-prod
```

---

## 🏋️ Exercice (~13 min)

`OrderService.placeOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

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

**1.1.** Ajoutez un champ `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `domain/`). La signature `placeOrder(Order)` reste inchangée.

**1.2.** Un `DiscountCodeService` est disponible dans la codebase. Ajoutez-le comme dépendance. Il fournit `checkDiscountCode(customerId, discountCode)` — retourne `true` si le code est disponible pour ce client.

**1.3.** Dans `placeOrder` : si un code de réduction est présent et **non disponible**, rejetez la commande.

📝 Ouvrez `OrderServiceTest` → méthode `p3_orderIsRejected_whenDiscountCodeIsAlreadyUsed`.
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

**2.1.** Si le code de réduction est présent et disponible, utilisez `PricingService.calculateTotal(items, discountCode)` à la place de l'appel existant.

📝 Ouvrez `OrderServiceTest` → méthode `p3_orderIsConfirmed_whenDiscountCodeIsValid`.
Les instructions et le scénario vous y attendent en commentaires.

---

### Étape 3 — Marquer le code comme utilisé après paiement

Objectif : après un paiement réussi, marquer le code comme utilisé. On met à jour le test de l'étape 2 avec une nouvelle assertion :

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET la commande est confirmée
ET le code de réduction est marqué comme utilisé   ← NOUVEAU
```

**3.1.** Appelez `DiscountCodeService.markAsUsed(customerId, discountCode)` après un paiement réussi.

📝 Ouvrez `OrderServiceTest` → méthode `p3_discountCodeIsMarkedAsUsed_afterSuccessfulPayment`.
Les instructions vous y attendent en commentaires.

---

### 💡 Conseils

- Utilisez `OrderBuilder` (dans `helper/`) pour construire les commandes de test — ça garde le bloc GIVEN focalisé sur ce qui compte

---

### 🤔 C'est quoi l'« unité » ici ?

Même code de production que la Part 2, mais un design de test différent. Les given helpers absorbent les changements d'API, et `PricingService` tourne pour de vrai. L'unité est toujours la classe — mais la frontière de ce qu'on mocke a bougé.

---

## ➡️ Suite

Passez à la **[Part 4](../part-4-behavior/README.fr.md)**