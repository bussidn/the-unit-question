# Part 1 - PowerMock Style

## 🎯 Objectif

Corriger un bug de performance **sans changer le comportement**.

## 📖 Contexte

Vous travaillez sur une boutique en ligne. L'équipe Ops a détecté un problème de performance : lors de la création d'une commande, le service fait **un appel base de données par produit** pour vérifier le stock.

Pour un panier de 10 articles → 10 requêtes. Problème classique **N+1**.

## 🐛 Le problème

Dans `StockService.checkAvailability()` :

```kotlin
fun checkAvailability(items: List<OrderItem>): Boolean {
    return items.all { item ->
        val stock = Database.getStock(item.productId)  // ❌ 1 appel par item
        stock.quantity >= item.quantity
    }
}
```

## 🔧 L'exercice

1. Utilisez `Database.getStockBatch()` (déjà disponible) pour récupérer tous les stocks en **un seul appel**
2. Adaptez `StockService.checkAvailability()` en conséquence
3. Lancez les tests : `./gradlew test`

## ⚠️ Contraintes

- **Ne modifiez PAS les tests** (pour l'instant)
- Le comportement doit rester **identique**
- Seule l'implémentation interne change

## 🤔 Débrief

- Comment s'est passé le refactoring ?
- Combien de tests ont cassé ?
- Est-ce que les tests vous ont aidé ?

---

## 💡 Aide

<details>
<summary>Signature de getStockBatch</summary>

```kotlin
Database.getStockBatch(productIds: List<String>): Map<String, Stock>
```

</details>

<details>
<summary>Solution</summary>

```kotlin
fun checkAvailability(items: List<OrderItem>): Boolean {
    val stocks = Database.getStockBatch(items.map { it.productId })
    return items.all { item ->
        val stock = stocks[item.productId]
        stock != null && stock.quantity >= item.quantity
    }
}
```

</details>

