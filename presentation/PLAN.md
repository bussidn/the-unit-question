# Plan de présentation (Live Coding)

> Format conférence avec démonstration de code en direct

## 🎬 Introduction (~5 min)

### Mon histoire
- "Au début de ma carrière, j'ai pris la notion de 'unit' très au sérieux"
- L'analogie de l'hélicoptère : tester chaque pièce sur un banc d'essai avant assemblage
- Conséquence logique : isoler chaque fonction, mocker toutes les dépendances
- "Je lisais 'don't' partout, mais jamais 'why'"

---

## 🔧 Partie 1 : L'isolation extrême (~10 min)

### Live coding
- Montrer un exemple de code avec PowerMock
- Mocker les `new`, les appels statiques
- "Regardez comme c'est... thorough"

### Problème évident
- Le test est un miroir du code
- Tout changement = test cassé
- "Ok, personne ne fait ça après un peu d'expérience"

---

## 🎭 Partie 2 : Les mocks classiques (~15 min)

### Live coding
- Même fonctionnalité, mocks "raisonnables"
- Montrer l'évolution A (codes promo)

### Observations
- C'est mieux, mais...
- Beaucoup de setup de mocks
- Refactoring interne = tests à maintenir

### Amélioration
- Factoriser les mocks dans des helpers
- "On peut réduire la maintenance"

---

## 💣 Partie 3 : Le piège (~10 min)

### Setup
- Deux services qui communiquent
- Chacun a ses tests (verts ✅)

### Live coding : Évolution B (backorder)
- Service A : "stock insuffisant → quantity: 0"
- Service B : "si null → backorder"

### Révélation
- Tous les tests passent
- En production : 💥
- "Comment attraper ce bug avec des mocks ?"

---

## ✨ Partie 4 : Behavior Testing (~10 min)

### Live coding
- Même code, tests au niveau comportement
- Refaire évolutions A et B

### Démonstration
- Refactoring interne → tests toujours verts
- Bug 0/null → attrapé immédiatement

---

## 🎯 Conclusion (~5 min)

### La morale
- Les tests sont une forme de **spécification**
- Une spec décrit le **What**, pas le **How**
- Tester l'implémentation = spécifier l'implémentation = s'interdire de changer

### L'analogie était fausse
- Le code n'est pas un hélicoptère
- Les "pièces" logicielles n'ont pas d'interfaces physiques stables
- La vraie "unit" = une unité de **comportement**

### Questions ouvertes
- Où placer la frontière ?
- Quand les mocks sont-ils acceptables ?
- Discussion avec le public

---

## 📂 Code utilisé

Voir le répertoire `kotlin-presentation/` pour le code de démonstration.

