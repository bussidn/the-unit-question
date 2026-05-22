# The Unit Question — Part 3: Clean mocks

## 🔄 What changed since Part 2

### Production code

The production code is identical to Part 2 — same `OrderService`, same dependencies, same behaviour.

### Test style

The biggest change is in **test design**. Compare the tests side-by-side with Part 2:

- Mockito is now integrated via `@ExtendWith(MockitoExtension.class)` and `@Mock` field annotations — no more manual `mock()` calls in `@BeforeEach`.
- An `OrderBuilder` is available in `helper/` to make test data more readable.
- Recurring setup patterns are factored into **given helpers** (`givenReservationSucceedsFor`, `givenPaymentSucceedsFor`, etc.).

These helpers absorb the cost of API changes. If `PaymentService.processPayment` adds a parameter tomorrow, you fix it in **one place** instead of every test that touches payment. That changes the economics of writing tests entirely.

You'll also notice that `PricingService` is no longer mocked — it's instantiated for real (`new PricingService()`). It's pure computation, no I/O, no side effects. Tests assert on real calculated values instead of arbitrary stubbed numbers.

### 🤔 Discussion: what is the "unit" here?

Open the tests from Part 2 next to Part 3. Same production code, same scenarios — but different design choices in the tests.

> **Questions to discuss:**
> - In Part 2, what happens when an API signature changes? How many tests do you have to touch?
> - Does that maintenance cost influence how many tests you write — or how you write them?
> - What is the "unit" in Part 3? Is it the same as Part 2?

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

