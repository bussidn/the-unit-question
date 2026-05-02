# The Unit Question — Part 3

## 🏋️ Exercise: Discount codes in order creation

**⏱️ ~25 minutes**

`PricingService` has been updated to support discount codes. It now exposes two methods:

- `calculateTotal(items)` — old API, no discount
- `calculateTotal(items, discountCode)` — new API with discount code

The tests for `PricingService` have already been written — look at `PricingServiceTest` to understand the expected behaviour and get inspired by the style.

`OrderService` has not been migrated yet. That's your mission.

---

### 🎯 Your mission

Migrate `OrderService` to support discount codes:

1. Add `DiscountCodeRepository` as a dependency
2. Change the `createOrder` signature to accept an optional `DiscountCode`
3. If a discount code is provided: verify it hasn't been used, calculate the price with the discount, mark it as used after payment
4. Write `OrderServiceTest` inspired by the style of `OrderCancellationServiceTest`

Run the tests: `./gradlew test`

---

### Scenarios to implement in `OrderServiceTest`

#### Scenario 1: Order without discount code

```
GIVEN an order with 2 items at €10 each
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

- Look at `OrderCancellationServiceTest` for style: `@ExtendWith`, `@Mock`, `@InjectMocks`, `given*` / `assert*` helpers
- The `OrderBuilder` is available in `helper/`
- Check `PricingServiceTest` to see how `PricingService` behaves

---

## ⏰ Once you're done

Open the file **[reveal/bug-report.md](reveal/bug-report.md)**

---

## ➡️ Next

Move on to **[Part 4](../part-4-behavior/README.md)**

