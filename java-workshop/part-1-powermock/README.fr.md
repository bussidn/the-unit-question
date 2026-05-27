# The Unit Question — Part 1

> **Qu'est-ce qu'un "unit" dans unit test ?**

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
| **Java (JDK)** | 21+ | `java -version` |
| **Git** | n'importe | `git --version` |

Gradle, JUnit et Mockito sont inclus (téléchargés automatiquement).

### Installation

```bash
git clone https://github.com/bussidn/the-unit-question.git
cd the-unit-question/java-workshop/part-1-powermock
./gradlew test
```

Vous devriez voir `BUILD SUCCESSFUL`. Si c'est le cas, vous êtes prêt !

> ⚠️ **Windows** : utilisez `gradlew.bat test`

---

## 🏋️ Exercice (~15 min)

`OrderService.placeOrder` ne supporte pas encore les codes de réduction. Votre mission : ajouter cette feature étape par étape, en écrivant les tests au fur et à mesure.

Regardez le `OrderServiceTest` existant pour le style et suivez les mêmes patterns.

Ouvrez `OrderServiceTest.java` — l'exercice commence juste après la Javadoc de la classe.

> 💡 Pour mocker un constructeur dans vos tests, utilisez :
> ```java
> MockedConstruction<SomeService> mocked = mockConstruction(SomeService.class, (mock, ctx) -> {
>     when(mock.someMethod(any())).thenReturn(...);
> });
> ```

---

### Étape 1 — Rejeter les codes de réduction déjà utilisés

Ajoutez un champ nullable `discountCode` au record `Order` (l'enum `DiscountCode` est déjà dans `domain/`). La signature `placeOrder(Order)` reste inchangée.

Un `DiscountCodeService` est disponible dans la codebase. Câblez-le comme nouvelle dépendance. Il fournit `checkDiscountCode(customerId, discountCode)` — retourne `true` si le code est disponible pour ce client.

Dans `placeOrder` : si un code de réduction est présent et **non disponible**, rejetez la commande.

**Test à écrire :**

```
ÉTANT DONNÉ une commande avec le code de réduction SUMMER20
ET ce code a déjà été utilisé par ce client

QUAND le client valide la commande

ALORS la commande est rejetée avec la raison "Discount code already used"
ET aucun paiement n'est déclenché
```

> 📝 Implémentez dans `OrderServiceTest` → cherchez **Step 1**

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

> 📝 Implémentez dans `OrderServiceTest` → cherchez **Step 2**

Lancez : `./gradlew test` ✅

---

### Étape 3 — Marquer le code comme utilisé après paiement

Après un paiement réussi, appelez `DiscountCodeService.markAsUsed(customerId, discountCode)`.

**Mettez à jour votre test précédent** pour asserter que le code est marqué comme utilisé.

> 📝 Implémentez dans `OrderServiceTest` → cherchez **Step 3**

Lancez : `./gradlew test` ✅

---

### 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on ajoute une feature à cette codebase.

---

### 🤔 C'est quoi l'« unité » ici ?

Dans cette partie, l'unité est **une seule méthode**. On mocke les autres classes, les méthodes statiques, et même les autres méthodes de la classe sous test (`whenPrivate` sur `isValid`). Chaque méthode est testée en isolation complète.

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Part 2](../part-2-light-mocks/README.fr.md)**

