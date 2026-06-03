---
marp: true
theme: default
paginate: true
header: "The Unit Question"
---


<!-- _class: lead -->

# 🎯 The Unit Question

## Qu'est-ce qu'un *« unit »* dans **unit test** ?


---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 26px; }
  h1 { margin-bottom: 0.6em; }
  ul { line-height: 1.8; }
</style>

# 🛠️ Setup

- 📦 **Repo** : `github.com/bussidn/the-unit-question`
- ☕ **Java 21** requis
- 💻 Votre **IDE préféré** (IntelliJ, VS Code, …)


---

<!-- _class: lead -->

# 👋 Qui parle ?

**Doriane Bussi** — *« la test freak »* 🧪

**Amélie Morille** — *« la test-curieuse »* 🔍️

👭 Leurs points communs : 
- diplomées de l'**ENSMA** 🎓 *(ingé méca & aéro ✈️)*
- développeuses **@sunday** 


---

<!-- _class: lead -->

# Partie 1
## Method isolation

> Une méthode sous test. **Tout le reste mocké.**

`mockConstruction` · `mockStatic` · `whenPrivate`


---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 26px; }
  h1 { margin-bottom: 0.6em; }
  ul { line-height: 1.8; }
</style>

# 💡 Conseils pratiques

- 🗂️ **Ouvrez chaque partie comme un projet séparé** dans votre IDE
- 🎯 **Travaillez par analogie** — copiez les patterns des tests existants
- ✏️ **Code de prod** : vous le refaites à chaque partie (c'est court)
- 🪂 **Parties 2, 3 & 4** : `git cherry-pick <SHA>` (cf. README) recopie le code de prod — filez directement aux tests


---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  table { font-size: 20px; }
  h1 { margin-bottom: 0.2em; }
  h2 { margin-top: 0.4em; margin-bottom: 0.2em; }
</style>

# 🏋️ Exercice Partie 1 — Codes de réduction

## Code de production

**`Order`** — ajouter un champ `DiscountCode` *(l'enum existe dans `domain/`)*
**`OrderService`** — câbler `DiscountCodeService` *(déjà fourni)* et modifier `placeOrder` :

| | Modification |
|---|---|
| **Étape 1** | Si `discountCode` présent et **non disponible** → rejeter `"Discount code already used"` |
| **Étape 2** | Si disponible → `calculateTotal(items, discountCode)` au lieu de `calculateTotal(items)` |
| **Étape 3** | Après paiement réussi → `markAsUsed(customerId, discountCode)` |

> `checkDiscountCode(customerId, code)` retourne `true` si le code est disponible

## Tests
3 squelettes `p1_*` dans `OrderServiceTest`.

💡 **Travaillez par analogie** avec les tests existants — suivez les mêmes patterns !


---

<!-- _class: lead -->

# Partie 2
## Light mocks

> Une petite injection de dépendance et **Mockito redevient lisible.**


---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  table { font-size: 19px; }
  table th, table td { padding: 3px 8px; }
  h1 { margin-bottom: 0.1em; }
  h2 { margin-top: 0.25em; margin-bottom: 0.1em; }
  blockquote { border-left: 4px solid #888; padding: 2px 12px; margin: 0.25em 0; font-size: 18px; }
  .cherry-pick { background: #fff4e0; border-left: 5px solid #f59e0b; padding: 5px 14px; margin: 0.3em 0; font-size: 18px; }
</style>

# 🏋️ Exercice Partie 2

<div class="cherry-pick">
🪂 <strong>Raccourci dispo</strong> — <code>git cherry-pick &lt;SHA&gt;</code> (cf. README) recopie le code de prod tel quel
</div>

## Code de production

**`Order`** — ajouter un champ `DiscountCode` *(l'enum existe dans `domain/`)*
**`OrderService`** — câbler `DiscountCodeService` *(déjà fourni)* et modifier `placeOrder` :

| | Modification |
|---|---|
| **Étape 1** | Si `discountCode` présent et **non disponible** → rejeter `"Discount code already used"` |
| **Étape 2** | Si disponible → `calculateTotal(items, discountCode)` au lieu de `calculateTotal(items)` |
| **Étape 3** | Après paiement réussi → `markAsUsed(customerId, discountCode)` |

> `checkDiscountCode(customerId, code)` retourne `true` si le code est disponible

## Tests
3 squelettes `p2_*` dans `OrderServiceTest`.

💡 Style cible : `@BeforeEach` configure le `OrderService`, mocks injectés au constructeur.
Plus de `mockConstruction`, `mockStatic`, `whenPrivate`.


---

<!-- _class: lead -->

# Partie 3
## Clean mocks

> Builders, given helpers, et **PricingService pour de vrai.**


---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  table { font-size: 19px; }
  table th, table td { padding: 3px 8px; }
  h1 { margin-bottom: 0.1em; }
  h2 { margin-top: 0.25em; margin-bottom: 0.1em; }
  blockquote { border-left: 4px solid #888; padding: 2px 12px; margin: 0.25em 0; font-size: 18px; }
  .cherry-pick { background: #fff4e0; border-left: 5px solid #f59e0b; padding: 5px 14px; margin: 0.3em 0; font-size: 18px; }
</style>

# 🏋️ Exercice Partie 3

<div class="cherry-pick">
🪂 <strong>Raccourci dispo</strong> — <code>git cherry-pick &lt;SHA&gt;</code> (cf. README) recopie le code de prod tel quel
</div>

## Code de production

**`Order`** — ajouter un champ `DiscountCode` *(l'enum existe dans `domain/`)*
**`OrderService`** — câbler `DiscountCodeService` *(déjà fourni)* et modifier `placeOrder` :

| | Modification |
|---|---|
| **Étape 1** | Si `discountCode` présent et **non disponible** → rejeter `"Discount code already used"` |
| **Étape 2** | Si disponible → `calculateTotal(items, discountCode)` au lieu de `calculateTotal(items)` |
| **Étape 3** | Après paiement réussi → `markAsUsed(customerId, discountCode)` |

> `checkDiscountCode(customerId, code)` retourne `true` si le code est disponible

## Tests
3 squelettes `p3_*` dans `OrderServiceTest`.

💡 Boîte à outils : `@Mock`, `OrderBuilder`, **écrivez vos** `givenDiscountCodeIs...()` helpers.
**`PricingService` est réel** (calcul pur) — pas de mock dessus.


---

<!-- _class: lead -->

# Partie 4
## Behavior tests

> Plus aucun mock. On teste **le comportement, pas les appels.**


---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  table { font-size: 19px; }
  table th, table td { padding: 3px 8px; }
  h1 { margin-bottom: 0.1em; }
  h2 { margin-top: 0.25em; margin-bottom: 0.1em; }
  blockquote { border-left: 4px solid #888; padding: 2px 12px; margin: 0.25em 0; font-size: 18px; }
  .cherry-pick { background: #fff4e0; border-left: 5px solid #f59e0b; padding: 5px 14px; margin: 0.3em 0; font-size: 18px; }
</style>

# 🏋️ Exercice Partie 4

<div class="cherry-pick">
🪂 <strong>Raccourci dispo</strong> — <code>git cherry-pick &lt;SHA&gt;</code> (cf. README) recopie le code de prod tel quel
</div>

## Code de production

**`Order`** — ajouter un champ `DiscountCode` *(l'enum existe dans `domain/`)*
**`OrderService`** — câbler `DiscountCodeService` *(déjà fourni)* et modifier `placeOrder` :

| | Modification |
|---|---|
| **Étape 1** | Si `discountCode` présent et **non disponible** → rejeter `"Discount code already used"` |
| **Étape 2** | Si disponible → `calculateTotal(items, discountCode)` au lieu de `calculateTotal(items)` |
| **Étape 3** | Après paiement réussi → `markAsUsed(customerId, discountCode)` |

> `checkDiscountCode(customerId, code)` retourne `true` si le code est disponible

## Tests
3 squelettes `p4_*` dans `OrderServiceTest`.

💡 `InMemoryDiscountCodeRepository` **déjà fourni** — wirez juste le `DiscountCodeService`.
Pas de `@Mock`, pas de `when().thenReturn()`, pas de `verify()`.
Assertions sur l'**état réel** des repos / gateways.


---

<!-- _class: lead -->

# 🎯 Débrief

<br>

## C'est quoi un *unit* pour vous, **maintenant** ?


---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 24px; }
  h1 { margin-bottom: 0.8em; }
  h2 { margin-top: 1em; margin-bottom: 0.3em; color: #444; }
  ul { font-size: 32px; line-height: 1.8; font-weight: 600; }
  p { font-size: 22px; line-height: 1.5; color: #666; }
</style>

# À quoi servent les tests unitaires ?

- Validation fonctionnelle
- Spécification
- Documentation technique

## Mais aussi

Non-régression, refactoring, aide à la conception via le TDD

