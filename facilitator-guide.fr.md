# Guide animateur — The Unit Question

Notes de présentation pour l'animateur. Ce document sert de fil conducteur à l'oral.
Les slides Marp sont le support visuel ; ce fichier contient le narratif et les points de discussion.

---

## 1. Introduction — Présenter l'atelier

### Message clé
> Tout le monde parle de "tests unitaires", mais personne n'est d'accord sur ce que "unit" veut dire.

### Origines

- Cet atelier est **issu d'une démarche personnelle**
  - Ce n'est pas un cours académique, c'est le fruit d'une réflexion sur nos propres pratiques
  - On considère que les approches vont "de moins bien à mieux" — c'est **opinionated**
  - Le parti pris : unit = behavior. Les 4 parties sont là pour **expérimenter la douleur** des autres approches et comprendre pourquoi on arrive à cette conclusion

### Structure de l'atelier

- **4 parties**, toujours le même exercice, toujours la même feature (codes de réduction)
- Le code de production est **identique entre les parties** (sauf Part 1 qui a une légère différence de design)
- Ce qui change : l'approche de test, la boîte à outils
- Les parties sont censées être **de plus en plus faciles** — si c'est le cas, c'est que l'approche est de plus en plus naturelle
- Avantage de la répétition : on ne perd pas de temps à re-découvrir un domaine métier, on se concentre sur le ressenti

### Conseils pratiques aux participants

- **Copiez votre code de prod** d'une partie à l'autre — c'est fait pour
- **Ouvrez chaque partie comme un projet séparé** dans votre IDE pour éviter les confusions de navigation (mauvais fichier, mauvaise part)
- L'objectif n'est **pas de finir** — c'est de ressentir les différences
- Travaillez **par analogie** avec les tests existants dans chaque part

---

## 2. Présentation de la Part 1 — Method isolation (PowerMock)

### L'analogie du banc d'essai

- Dans l'industrie aéronautique, on teste les pièces sur des **bancs d'essai**
  - On isole une pièce (un réacteur, un train d'atterrissage) de l'avion complet
  - On la branche sur des simulateurs qui reproduisent les conditions de vol
  - Chaque entrée/sortie est contrôlée, chaque capteur est monitoré
- C'est exactement ce que fait la Part 1 avec le code :
  - On prend une **méthode** (l'unité)
  - On remplace **toutes** ses dépendances par des simulateurs (mocks)
  - On contrôle chaque entrée, on vérifie chaque sortie

### Le parallèle avec PowerMock / mockConstruction

- `mockConstruction` = le banc d'essai
  - On intercepte la création de chaque dépendance
  - On la remplace par un simulateur contrôlé
- Le test ne touche **que** la méthode `placeOrder` — tout le reste est simulé

### Points de discussion à faire émerger (après l'exercice)

- C'était quoi la "unit" ici ?
- Est-ce que ça vous a semblé naturel ?
- Combien de lignes de setup pour combien de lignes d'assertion ?
- Si on renomme une méthode interne, que se passe-t-il ?
- Est-ce que le test vous protège vraiment contre des régressions ?

---

## 3. Transition Part 1 → Part 2

*TODO — à compléter après validation de la structure*

---

## 4. Présentation de la Part 2 — Light mocks

*TODO*

---

## 5. Transition Part 2 → Part 3

*TODO*

---

## 6. Présentation de la Part 3 — Clean mocks (Mockito annotations)

*TODO*

---

## 7. Transition Part 3 → Part 4

*TODO*

---

## 8. Présentation de la Part 4 — Behaviour tests (No mocks)

*TODO*

---

## 9. Débrief final

### Questions de synthèse

- Quelle partie vous a semblé la plus naturelle ?
- Dans laquelle avez-vous le plus confiance pour détecter une vraie régression ?
- Laquelle survivrait le mieux à un refactoring du code de prod ?
- Laquelle reliriez-vous dans 6 mois en comprenant immédiatement ce qui est testé ?

---

## Annexes

### Timing indicatif

| Phase | Durée |
|-------|-------|
| Introduction | 10 min |
| Part 1 (exercice + débrief) | 25 min |
| Part 2 (exercice + débrief) | 20 min |
| Part 3 (exercice + débrief) | 20 min |
| Part 4 (exercice + débrief) | 20 min |
| Débrief final | 15 min |
| **Total** | **~2h** |

