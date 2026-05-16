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

## 🏋️ Exercice : Codes de réduction dans la création de commande

**⏱️ ~25 minutes**

`PricingService` a été mis à jour pour supporter les codes de réduction. Il expose maintenant deux méthodes :

- `calculateTotal(items)` — ancienne API, sans réduction
- `calculateTotal(items, discountCode)` — nouvelle API avec code de réduction

`OrderService` n'a pas encore été migré. C'est votre mission.

---

### 🎯 Votre mission

`OrderService.placeOrder` doit supporter les codes de réduction.

- Ajouter `DiscountCodeService` comme dépendance (l'instancier dans `createOrder`, comme les autres services)
- Modifier la signature de `createOrder` pour accepter un `DiscountCode` optionnel
- Si un code de réduction est fourni : utiliser `DiscountCodeService.checkDiscountCode` — s'il retourne `true`, le code est disponible et peut être appliqué ; sinon rejeter. Calculer le prix avec la réduction, marquer comme utilisé après le paiement
- Ajouter vos nouveaux scénarios de test au `OrderServiceTest` existant — suivez le style déjà en place

Lancer les tests : `./gradlew test`

---

### Scénarios à implémenter dans `OrderServiceTest`

#### Scénario 1 : Commande avec un code de réduction valide

```
ÉTANT DONNÉ une commande avec 2 articles à 55€ chacun (sous-total : 110€)
ET le code de réduction SUMMER20 (-20%)
ET le code n'a pas encore été utilisé par ce client

QUAND le client valide la commande

ALORS le paiement est effectué pour 105.60€
ET le code de réduction est marqué comme utilisé
ET la commande est confirmée
```

#### Scénario 2 : Code de réduction déjà utilisé

```
ÉTANT DONNÉ une commande avec le code de réduction SUMMER20
ET ce code a déjà été utilisé par ce client

QUAND le client valide la commande

ALORS la commande est rejetée avec la raison "Discount code already used"
ET aucun paiement n'est déclenché
```
---

### 💡 Conseils

- Regardez le `OrderServiceTest` existant pour le style et suivez les mêmes patterns
- Pour mocker un constructeur dans vos tests, utilisez :
  ```java
  MockedConstruction<DiscountCodeService> mocked = mockConstruction(DiscountCodeService.class, (mock, ctx) -> {
      when(mock.checkDiscountCode(any(), any())).thenReturn(false);
  });
  ```
- Pour mocker les méthodes statiques de `Database` :
  ```java
  MockedStatic<Database> mockedDb = mockStatic(Database.class);
  ```

### 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on ajoute une feature à cette codebase.

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Part 2](../part-2-light-mocks/README.fr.md)**

