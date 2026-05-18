# The Unit Question — Part 3 : Clean mocks

## 🔄 Ce qui a changé depuis la Part 2

Le **code de production est identique à la Part 2** — même `OrderService`, mêmes dépendances, même comportement.

Ce qui a changé, c'est l'écriture des tests :

- Mockito est maintenant intégré via `@ExtendWith(MockitoExtension.class)` et les annotations `@Mock` sur les champs. Plus d'appels `mock()` manuels dans `@BeforeEach` — Mockito crée et injecte les mocks automatiquement avant chaque test.
- Un `OrderBuilder` est maintenant disponible dans `helper/` pour rendre la construction des données de test plus lisible et expressive.

Ouvrez le `OrderServiceTest` existant et comparez-le côte à côte avec la version de la Part 2. Repérez-vous chaque différence ? Cette comparaison, c'est la moitié de l'apprentissage.

---

## 🏋️ Exercice : Codes promo dans `placeOrder`

**⏱️ ~25 minutes**

Un `DiscountCodeService` a été ajouté à la codebase. Il expose deux méthodes :

- **`checkDiscountCode(customerId, discountCode)`** — retourne `true` si le code est disponible pour ce client
- **`markAsUsed(customerId, discountCode)`** — marque le code comme utilisé pour ce client

`PricingService` a aussi été mis à jour avec une nouvelle surcharge : `calculateTotal(items, discountCode)` qui applique la réduction au prix.

`OrderService.placeOrder` n'a pas encore été migré. C'est votre mission.

---

### 🎯 Votre mission

`OrderService.placeOrder` doit supporter les codes promo.

- Ajouter `DiscountCodeService` comme dépendance
- Ajouter un champ `discountCode` au record `Order` — il doit être nullable (l'enum `DiscountCode` est déjà fourni dans `domain/`). La signature `placeOrder(Order)` reste inchangée
- Si un code promo est fourni : utiliser `DiscountCodeService.checkDiscountCode` — si ça retourne `true`, le code est disponible et peut être appliqué ; sinon, rejetez. Calculez le prix avec remise, marquez-le comme utilisé après paiement
- Ajoutez vos nouveaux scénarios de test dans le `OrderServiceTest` existant — suivez le style déjà en place

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

---

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour le style et suivez les mêmes patterns
- Utilisez `OrderBuilder` (dans `helper/`) pour construire les commandes de test — ça garde le bloc GIVEN focalisé sur ce qui compte

---

## ➡️ Suite

Passez à la **[Part 4](../part-4-behavior/README.fr.md)**