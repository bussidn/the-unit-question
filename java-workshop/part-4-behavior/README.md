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

Look at `OrderCancellationServiceTest` — it has been transformed into a behaviour test. It shows you how to:
- Wire real implementations by hand in `@BeforeEach`
- Use `InMemoryOrderRepository` and `InMemoryStockRepository`
- Keep `@Mock` only on `PaymentGateway` and `ShippingGateway`

`InMemoryOrderRepository`, `InMemoryStockRepository` and `InMemoryDiscountCodeRepository` are available in `helper/`.

`PromoCodeService` is provided with its tests — use `PromoCodeService.checkPromoCode(customerId, discountCode)`: if it returns `true`, the code is available for use.

---

## 🎯 Your mission

1. Migrate `OrderService` to support discount codes — add `PromoCodeService` as a dependency, use `checkPromoCode` to validate, `markAsUsed` after payment
2. Write `OrderServiceTest` in behaviour test style

Run the tests: `./gradlew test`

---

### Scenarios to implement in `OrderServiceTest`

#### Scenario 1: Order without discount code

```
GIVEN an order with 2 items at €10 each
AND sufficient stock
AND no discount code

WHEN the customer places the order

THEN payment is processed for the amount calculated by PricingService
AND the order is confirmed with a shipment
```

#### Scenario 2: Order with a valid discount code

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

#### Scenario 3: Discount code already used

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

#### Scenario 4: Insufficient stock

```
GIVEN an order where stock is unavailable

WHEN the customer places the order

THEN the order is rejected with reason "Insufficient stock"
AND neither pricing nor payment are called
```

---

### 💡 Tips

- Look at `OrderCancellationServiceTest` to see how to wire real implementations
- `PricingService` has no dependencies — instantiate it directly: `new PricingService()`
- Keep `@Mock` only on `PaymentGateway` and `ShippingGateway`
- Check `PricingServiceTest` to understand how `PricingService` behaves

---

## ➡️ End of workshop 🎉

