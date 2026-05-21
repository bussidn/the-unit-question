# The Unit Question — Part 4: Behaviour tests

## 🏋️ Exercise (~25 min)

Same exercise as Part 3 — same feature, same production code to migrate.

The difference is in the tests.

---

## 🔄 What changed since Part 3

In previous parts, every collaborator of `OrderService` was mocked. Each mock silently encoded an assumption about what `PricingService`, `StockService`, etc. would do.

In Part 4, we remove those assumptions. Tests use **real implementations** of internal services. Only **external gateways** remain mocked — they represent third-party systems we don't control.

For repositories, we use **fakes**: simple in-memory implementations, no database.

The `OrderBuilder` is still available — it carries over from Part 3 and works the same way here.

---

## What is provided

Look at the existing `OrderServiceTest` — it has been transformed into a behaviour test. It shows you how to:
- Wire real implementations by hand in `@BeforeEach`
- Use `InMemoryStockRepository` and in-memory gateways
- Assert on real behaviour instead of mock interactions

`InMemoryOrderRepository`, `InMemoryStockRepository` and `InMemoryDiscountCodeRepository` are available in `helper/`.

`DiscountCodeService` is provided with its tests. It exposes two methods:

- **`checkDiscountCode(customerId, discountCode)`** — returns `true` if the code is available for this customer
- **`markAsUsed(customerId, discountCode)`** — marks the code as used for this customer

`PricingService` has also been updated with a new overload: `calculateTotal(items, discountCode)` that applies the discount to the price.

---

## 🎯 Your mission

`OrderService.placeOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

Look at the existing `OrderServiceTest` for style and follow the same patterns.

---

### Step 1 — Reject already-used discount codes

Add a nullable `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `domain/`). The `placeOrder(Order)` signature stays the same.

Add `DiscountCodeService` as a dependency. In `placeOrder`: if a discount code is present and **not available**, reject the order.

**Test to write:**

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

Run: `./gradlew test` ✅

---

### Step 2 — Apply the discount to the price

If the discount code is present and available, use `PricingService.calculateTotal(items, discountCode)` instead of the existing call.

**Test to write:**

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

WHEN the customer places the order

THEN payment is processed for €105.60
AND the order is confirmed
```

Run: `./gradlew test` ✅

---

### Step 3 — Mark the code as used after payment

After successful payment, call `DiscountCodeService.markAsUsed(customerId, discountCode)`.

**Update your previous test** to assert the code is marked as used.

Run: `./gradlew test` ✅

---

### 💡 Tips

- Look at the existing `OrderServiceTest` to see how real implementations are wired
- `PricingService` has no dependencies — instantiate it directly: `new PricingService()`

---

## ➡️ End of workshop 🎉

