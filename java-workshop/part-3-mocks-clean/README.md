# The Unit Question — Part 3

## 🏋️ Exercise: Order Cancellation

**⏱️ ~20 minutes** *(don't worry if you don't finish)*

We'd like to allow customers to cancel an order. The system should handle the refund, release stock, and cancel shipping if needed.

### Scenario 1: Cancellation before shipping

```
GIVEN a confirmed order
AND a payment was made (transaction ID present)
AND not yet shipped (no tracking number)

WHEN the customer cancels the order

THEN the payment is refunded
AND the stock is released
AND the status becomes CANCELLED
```

### Scenario 2: Cancellation with shipping in progress

```
GIVEN a confirmed order
AND a payment was made
AND a shipment was created (tracking number present)

WHEN the customer cancels the order

THEN the payment is refunded
AND the stock is released
AND the shipment is cancelled
AND the status becomes CANCELLED
```

### Your mission

1. Look at the existing code in `src/main/java/service/`
2. Look at the existing tests — notice the helpers and builders
3. Implement the feature with its tests
4. Run the tests: `./gradlew test`

### 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you add a feature to this codebase.

---

## ⏰ Time's up?

Once done (or time's up), open the file **[reveal/bug-report.md](reveal/bug-report.md)**

---

## ➡️ Next

Move on to **[Part 4](../part-4-behavior/README.md)**

