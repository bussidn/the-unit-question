# The Unit Question — Part 3: Clean mocks

## 🔄 What changed since Part 2

The **production code is identical to Part 2** — same `OrderService`, same dependencies, same behaviour.

What changed is how the tests are written:

- Mockito is now integrated via `@ExtendWith(MockitoExtension.class)` and `@Mock` field annotations. There are no more manual `mock()` calls inside `@BeforeEach` — Mockito creates and injects the mocks automatically before each test.
- An `OrderBuilder` is now available in `helper/` to make test data setup more readable and intention-revealing.

Open the existing `OrderServiceTest` and compare it side-by-side with Part 2's version. Can you spot every difference? That comparison is half the learning here.

---

## 🏋️ Exercise (~25 min)

`OrderService.placeOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

Look at the existing `OrderServiceTest` for style and follow the same patterns.

---

### Step 1 — Reject already-used discount codes

Add a nullable `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `domain/`). The `placeOrder(Order)` signature stays the same.

A `DiscountCodeService` is available in the codebase. Add it as a dependency. It provides `checkDiscountCode(customerId, discountCode)` — returns `true` if the code is available for this customer.

In `placeOrder`: if a discount code is present and **not available**, reject the order.

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

- Use `OrderBuilder` (in `helper/`) to build test orders — it keeps the GIVEN block focused on what matters

---

## ➡️ Next

Move on to **[Part 4](../part-4-behavior/README.md)**

