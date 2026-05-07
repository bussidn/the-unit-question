# The Unit Question — Part 4

## 🏋️ Exercise: Discount codes in order creation

**⏱️ ~25 minutes**

Same exercise as Part 3 — same feature, same production code to migrate.

The difference is in the tests.

---

## What changes

In previous parts, every collaborator of `OrderService` was mocked. Each mock silently encoded an assumption about what `PricingService`, `StockService`, etc. would do.

In Part 4, we remove those assumptions. Tests use **real implementations** of internal services. Only **external gateways** remain mocked — they represent third-party systems we don't control.

For repositories, we use **fakes**: simple in-memory implementations, no database.

---

## What is provided

Look at the existing `OrderServiceTest` — it has been transformed into a behaviour test. It shows you how to:
- Wire real implementations by hand in `@BeforeEach`
- Use `InMemoryStockRepository` and in-memory gateways
- Assert on real behaviour instead of mock interactions

`InMemoryOrderRepository`, `InMemoryStockRepository` and `InMemoryDiscountCodeRepository` are available in `helper/`.

`DiscountCodeService` is provided with its tests — use `DiscountCodeService.checkDiscountCode(customerId, discountCode)`: if it returns `true`, the code is available for use.

---

## 🎯 Your mission

1. Migrate `OrderService` to support discount codes — add `DiscountCodeService` as a dependency, use `checkDiscountCode` to validate, `markAsUsed` after payment
2. Write `OrderServiceTest` in behaviour test style

Run the tests: `./gradlew test`

---

### Scenarios to implement in `OrderServiceTest`

#### Scenario 1: Order with a valid discount code

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer
AND sufficient stock

WHEN the customer places the order

THEN payment is processed for €105.60
AND the discount code is marked as used
AND the order is confirmed
```

#### Scenario 2: Discount code already used

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

#### Scenario 3: Insufficient stock

```
GIVEN an order where stock is unavailable

WHEN the customer places the order

THEN the order is rejected with reason "Insufficient stock"
AND neither pricing nor payment are called
```

---

### 💡 Tips

- Look at the existing `OrderServiceTest` to see how real implementations are wired
- `PricingService` has no dependencies — instantiate it directly: `new PricingService()`

---

## ➡️ End of workshop 🎉

