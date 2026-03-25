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

## 🏋️ Exercice : Implémenter l'envoi d'email

**⏱️ ~15 minutes** *(ne vous inquiétez pas si vous ne finissez pas)*

On voudrait que le client reçoive un email de confirmation après avoir passé sa commande.

**Actuellement, l'objet `Order` ne contient pas l'adresse email du client.** Vous devrez l'ajouter pour pouvoir envoyer la confirmation.

L'email doit contenir l'identifiant de la commande (`id`) et le montant total. Si la commande échoue, aucun email n'est envoyé.

### Scénario 1 : Commande réussie

```
ÉTANT DONNÉ un client avec l'email "alice@example.com"
ET un panier contenant 2 articles pour un total de 49.99€
ET le stock est disponible
ET le paiement est accepté

QUAND le client valide sa commande

ALORS la commande est créée avec succès
ET un email est envoyé à "alice@example.com"
ET l'email contient "Commande ORDER-123 confirmée : 49.99€"
```

### Scénario 2 : Paiement refusé

```
ÉTANT DONNÉ un client avec l'email "bob@example.com"
ET un panier contenant 1 article
ET le stock est disponible
ET le paiement est refusé

QUAND le client valide sa commande

ALORS la commande échoue
ET aucun email n'est envoyé
```

### Votre mission

1. Regardez le code existant dans `src/main/java/service/OrderService.java`
2. Implémentez la feature comme bon vous semble
3. Lancez les tests

### 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on ajoute une feature à cette codebase.

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Part 2](../part-2-light-mocks/README.fr.md)**

