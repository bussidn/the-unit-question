# The Unit Question — Part 3 : Clean mocks

## 🔄 Ce qui a changé depuis la Part 2

Le **code de production est identique à la Part 2** — même `OrderService`, mêmes dépendances, même comportement.

Ce qui a changé, c'est l'écriture des tests :

- Mockito est maintenant intégré via `@ExtendWith(MockitoExtension.class)` et les annotations `@Mock` sur les champs. Plus d'appels `mock()` manuels dans `@BeforeEach` — Mockito crée et injecte les mocks automatiquement avant chaque test.
- Un `OrderBuilder` est maintenant disponible dans `helper/` pour rendre la construction des données de test plus lisible et expressive.

Ouvrez le `OrderServiceTest` existant et comparez-le côte à côte avec la version de la Part 2. Repérez-vous chaque différence ? Cette comparaison, c'est la moitié de l'apprentissage.

---

## 🏋️ Exercice (~25 min)

`OrderService.placeOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

Regardez le `OrderServiceTest` existant pour le style et suivez les mêmes patterns.

---

### Étape 1 — Rejeter les codes de réduction déjà utilisés

Ajoutez un champ nullable `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `domain/`). La signature `placeOrder(Order)` reste inchangée.

Un `DiscountCodeService` est disponible dans la codebase. Ajoutez-le comme dépendance. Il fournit `checkDiscountCode(customerId, discountCode)` — retourne `true` si le code est disponible pour ce client.

Dans `placeOrder` : si un code de réduction est présent et **non disponible**, rejetez la commande.

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

- Utilisez `OrderBuilder` (dans `helper/`) pour construire les commandes de test — ça garde le bloc GIVEN focalisé sur ce qui compte

---

## ➡️ Suite

Passez à la **[Part 4](../part-4-behavior/README.fr.md)**