# The Unit Question — Part 3: Clean mocks

## 🔄 What changed since Part 2

The **production code is identical to Part 2** — same `OrderService`, same dependencies, same behaviour.

What changed is how the tests are written:

- Mockito is now integrated via `@ExtendWith(MockitoExtension.class)` and `@Mock` field annotations. There are no more manual `mock()` calls inside `@BeforeEach` — Mockito creates and injects the mocks automatically before each test.
- An `OrderBuilder` is now available in `helper/` to make test data setup more readable and intention-revealing.

Open the existing `OrderServiceTest` and compare it side-by-side with Part 2's version. Can you spot every difference? That comparison is half the learning here.

---

## 🏋️ Exercise: Discount codes in `placeOrder`

**⏱️ ~25 minutes**

A `DiscountCodeService` has been added to the codebase. It exposes two methods:

- **`checkDiscountCode(customerId, discountCode)`** — returns `true` if the code is available for this customer
- **`markAsUsed(customerId, discountCode)`** — marks the code as used for this customer

`PricingService` has also been updated with a new overload: `calculateTotal(items, discountCode)` that applies the discount to the price.

`OrderService.placeOrder` has not been migrated yet. That's your mission.

---

### 🎯 Your mission

`OrderService.placeOrder` needs to support discount codes.

- Add `DiscountCodeService` as a dependency
- Add a `discountCode` field to the `Order` record — it should be nullable (the `DiscountCode` enum is already provided in `domain/`). The `placeOrder(Order)` signature stays the same
- If a discount code is provided: use `DiscountCodeService.checkDiscountCode` — if it returns `true`, the code is available and can be applied; otherwise reject. Calculate the price with the discount, mark as used after payment
- Add your new test scenarios to the existing `OrderServiceTest` — follow the style already in place

Run the tests: `./gradlew test`

---

### Scenarios to implement in `OrderServiceTest`

#### Scenario 1: Order with a valid discount code

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

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

---

### 💡 Tips

- Look at the existing `OrderServiceTest` for style and follow the same patterns
- Use `OrderBuilder` (in `helper/`) to build test orders — it keeps the GIVEN block focused on what matters

---

## ➡️ Next

Move on to **[Part 4](../part-4-behavior/README.md)**

