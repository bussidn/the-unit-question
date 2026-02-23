# Part 1 - PowerMock Style

## 🎯 Goal

Fix a performance bug **without changing the behavior**.

## 📖 Context

You're working on an online shop. The Ops team detected a performance issue: when creating an order, the service makes **one database call per product** to check stock availability.

For a cart with 10 items → 10 queries. Classic **N+1 problem**.

## 🐛 The problem

In `StockService.checkAvailability()`:

```kotlin
fun checkAvailability(items: List<OrderItem>): Boolean {
    return items.all { item ->
        val stock = Database.getStock(item.productId)  // ❌ 1 call per item
        stock.quantity >= item.quantity
    }
}
```

## 🔧 Exercise

1. Use `Database.getStockBatch()` (already available) to fetch all stocks in **a single call**
2. Update `StockService.checkAvailability()` accordingly
3. Run the tests: `./gradlew test`

## ⚠️ Constraints

- **Do NOT modify the tests** (for now)
- The behavior must remain **identical**
- Only the internal implementation changes

## 🤔 Debrief

- How many tests broke?
- How long did it take to fix them?
- What did you have to change?

---

## 💡 Hints

<details>
<summary>getStockBatch signature</summary>

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

