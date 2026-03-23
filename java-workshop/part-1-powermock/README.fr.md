# The Unit Question — Atelier Java

> **Qu'est-ce qu'un "unit" dans unit test ?**

## 🎯 À propos de cet atelier

Tout le monde parle de "tests unitaires", mais personne n'est d'accord sur ce qu'est une "unité".

- Est-ce une méthode ? Une classe ? Un module ?
- Faut-il tout mocker ? Rien mocker ? Un juste milieu ?
- Pourquoi certaines suites de tests cassent à chaque refactoring alors que d'autres survivent à des changements majeurs ?

**Cet atelier vous fait vivre différentes approches de test** à travers des exercices pratiques. Vous ressentirez les frictions de chaque stratégie et découvrirez laquelle vous convient.

### Les 4 parties

| Partie | Unité = | Point de friction |
|--------|---------|-------------------|
| **Part 1** | La méthode/classe | Tout est mocké → tests couplés à l'implémentation |
| **Part 2** | La couche technique | Tests mal conçus → changements d'API pénibles |
| **Part 3** | La couche technique | Tests bien conçus → mais bugs de contrat cachés |
| **Part 4** | Le comportement métier | Les tests décrivent le QUOI, pas le COMMENT |

---

## 🛠️ Prérequis

### Obligatoire

| Outil | Version | Commande de vérification |
|-------|---------|--------------------------|
| **Java (JDK)** | 21+ | `java -version` |
| **Git** | n'importe | `git --version` |

### Inclus (pas d'installation nécessaire)

- **Gradle 9.3** — via le wrapper (`./gradlew`)
- **JUnit 5.11** — téléchargé par Gradle
- **Mockito 5.14** — téléchargé par Gradle

---

## 📦 Avant de commencer

### 1. Cloner le dépôt

```bash
git clone https://github.com/bussidn/the-unit-question.git
cd the-unit-question/java-workshop/part-1-powermock
```

### 2. Vérifier l'installation

Lancez les tests pour vérifier que tout fonctionne :

```bash
./gradlew test
```

Vous devriez voir :

```
BUILD SUCCESSFUL
```

> ⚠️ **Sous Windows**, utilisez `gradlew.bat test` à la place.

### 3. Ouvrir dans votre IDE

Ouvrez le dossier `part-1-powermock` dans votre IDE favori (IntelliJ IDEA recommandé).

Vérifiez que votre IDE :
- Reconnaît le projet comme un projet Gradle
- Utilise le JDK 21+
- Peut lancer les tests

---

## ✅ Vous êtes prêt !

Si les tests passent, vous êtes paré pour l'atelier.

**À bientôt !** 🚀

---

## 📁 Structure du projet

```
part-1-powermock/
├── src/
│   ├── main/java/           # Code de production
│   │   ├── domain/          # Objets métier (Order, Stock, etc.)
│   │   ├── infrastructure/  # Systèmes externes (Database, APIs)
│   │   ├── repository/      # Accès aux données
│   │   └── service/         # Logique métier
│   │
│   └── test/java/           # Code de test
│       └── service/         # Tests des services
│
├── build.gradle             # Dépendances & configuration
└── gradlew                  # Wrapper Gradle (utilisez celui-ci !)
```

---

## ❓ Dépannage

### "java: error: release version 21 not supported"

Votre IDE utilise un JDK trop ancien. Configurez-le pour utiliser JDK 21+.

**IntelliJ** : File → Project Structure → Project SDK → 21

### "Could not resolve dependencies"

Vérifiez votre connexion internet. Gradle doit télécharger les dépendances.

```bash
./gradlew --refresh-dependencies test
```

### Les tests échouent avec "UnsupportedClassVersionError"

Vous utilisez une vieille version de Java. Vérifiez `java -version` et installez JDK 21+.

