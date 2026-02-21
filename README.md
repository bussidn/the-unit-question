# The Unit Question

> Qu'est-ce qu'un "unit" dans unit test ?

## 🎯 À propos

Ce repository contient le matériel pour un atelier (ou une présentation) sur les heuristiques de test.

**Le sujet** : La notion de "unit" dans les tests unitaires est galvaudée. Chacun y met sa propre définition. Ce workshop vous fait vivre différentes approches de test pour découvrir par vous-même laquelle vous convient.

## 📁 Structure

```
├── presentation/          # Support et plan de présentation (live coding)
│
├── java/                  # Atelier en Java
├── javascript/            # Atelier en JavaScript
├── python/                # Atelier en Python
├── kotlin-workshop/       # Atelier en Kotlin
│   ├── part-1-powermock/
│   ├── part-2-light-mocks/
│   ├── part-3-mocks-clean/
│   └── part-4-behavior/
│
└── kotlin-presentation/   # Code de démo pour le format conférence
```

## 🛠️ Prérequis

Selon le langage choisi :
- **Java** : JDK 17+, Maven ou Gradle
- **JavaScript** : Node.js 18+
- **Python** : Python 3.10+
- **Kotlin** : JDK 17+, Gradle

## 🚀 Déroulement de l'atelier (1h45)

| Phase | Durée | Description |
|-------|-------|-------------|
| Introduction | ~10 min | Histoire personnelle et analogie |
| Part 1 - PowerMock | ~15 min | L'isolation extrême (exercice léger) |
| Part 2 - Light Mocks | ~25 min | Évolution A avec mocks classiques |
| Transition | ~5 min | Amélioration : factoriser les mocks |
| Part 3 - Mocks Clean | ~25 min | Évolution B (même inputs, output différent) |
| Part 4 - Behavior | ~20 min | Refaire les évolutions A et B |
| Conclusion | ~5 min | La morale : What vs How |

## 📝 Évolutions à implémenter

### Évolution A
*Ajouter la gestion des codes promo*

### Évolution B  
*Quand le stock est insuffisant, passer la commande en "backorder" au lieu de la rejeter*

## 📄 Licence

MIT

