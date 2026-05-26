---
marp: true
theme: default
paginate: true
header: "The Unit Question"
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

# 🏋️ Exercice — Codes de réduction

## Code de production

**`Order`** — ajouter un champ nullable `DiscountCode` *(l'enum existe dans `domain/`)*
**`OrderService`** — câbler `DiscountCodeService` *(déjà fourni)* et modifier `placeOrder` :

| | Modification |
|---|---|
| **Étape 1** | Si `discountCode` présent et **non disponible** → rejeter `"Discount code already used"` |
| **Étape 2** | Si disponible → `calculateTotal(items, discountCode)` au lieu de `calculateTotal(items)` |
| **Étape 3** | Après paiement réussi → `markAsUsed(customerId, discountCode)` |

> `checkDiscountCode(customerId, code)` retourne `true` si le code est disponible

## Tests

Complétez les squelettes existants dans `OrderServiceTest`.

💡 **Travaillez par analogie** avec les tests existants — suivez les mêmes patterns !
